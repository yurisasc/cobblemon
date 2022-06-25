package com.cablemc.pokemoncobbled.common.pokemon

import com.cablemc.pokemoncobbled.common.CobbledNetwork.sendToPlayers
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.PokemonCobbled.LOGGER
import com.cablemc.pokemoncobbled.common.api.abilities.Abilities
import com.cablemc.pokemoncobbled.common.api.abilities.Ability
import com.cablemc.pokemoncobbled.common.api.events.CobbledEvents
import com.cablemc.pokemoncobbled.common.api.events.pokemon.PokemonFaintedEvent
import com.cablemc.pokemoncobbled.common.api.moves.BenchedMove
import com.cablemc.pokemoncobbled.common.api.moves.BenchedMoves
import com.cablemc.pokemoncobbled.common.api.moves.MoveSet
import com.cablemc.pokemoncobbled.common.api.moves.MoveTemplate
import com.cablemc.pokemoncobbled.common.api.moves.Moves
import com.cablemc.pokemoncobbled.common.api.pokeball.PokeBalls
import com.cablemc.pokemoncobbled.common.api.pokemon.Natures
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonPropertyExtractor
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.api.pokemon.aspect.AspectProvider
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.Evolution
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.EvolutionController
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.EvolutionDisplay
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.EvolutionProxy
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.PreEvolution
import com.cablemc.pokemoncobbled.common.api.pokemon.experience.ExperienceGroup
import com.cablemc.pokemoncobbled.common.api.pokemon.feature.SpeciesFeature
import com.cablemc.pokemoncobbled.common.api.pokemon.stats.Stat
import com.cablemc.pokemoncobbled.common.api.pokemon.stats.Stats
import com.cablemc.pokemoncobbled.common.api.pokemon.status.Statuses
import com.cablemc.pokemoncobbled.common.api.reactive.Observable
import com.cablemc.pokemoncobbled.common.api.reactive.SettableObservable
import com.cablemc.pokemoncobbled.common.api.reactive.SimpleObservable
import com.cablemc.pokemoncobbled.common.api.storage.StoreCoordinates
import com.cablemc.pokemoncobbled.common.api.storage.party.PlayerPartyStore
import com.cablemc.pokemoncobbled.common.api.types.ElementalType
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.net.messages.client.PokemonUpdatePacket
import com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update.*
import com.cablemc.pokemoncobbled.common.pokeball.PokeBall
import com.cablemc.pokemoncobbled.common.pokemon.activestate.ActivePokemonState
import com.cablemc.pokemoncobbled.common.pokemon.activestate.InactivePokemonState
import com.cablemc.pokemoncobbled.common.pokemon.activestate.PokemonState
import com.cablemc.pokemoncobbled.common.pokemon.activestate.SentOutState
import com.cablemc.pokemoncobbled.common.pokemon.evolution.CobbledEvolutionProxy
import com.cablemc.pokemoncobbled.common.pokemon.status.PersistentStatus
import com.cablemc.pokemoncobbled.common.pokemon.status.PersistentStatusContainer
import com.cablemc.pokemoncobbled.common.util.DataKeys
import com.cablemc.pokemoncobbled.common.util.getServer
import com.cablemc.pokemoncobbled.common.util.lang
import com.cablemc.pokemoncobbled.common.util.readSizedInt
import com.cablemc.pokemoncobbled.common.util.sendServerMessage
import com.cablemc.pokemoncobbled.common.util.writeSizedInt
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.util.UUID
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.random.Random
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement.COMPOUND_TYPE
import net.minecraft.nbt.NbtList
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper.ceil
import net.minecraft.util.math.MathHelper.clamp
import net.minecraft.util.math.Vec3d

open class Pokemon {
    var uuid = UUID.randomUUID()
    var species = PokemonSpecies.EEVEE
        set(value) {
            val quotient = clamp(currentHealth / hp.toFloat(), 0F, 1F)
            val previousFeatureKeys = species.features
            field = value
            val newFeatureKeys = species.features
            val addedFeatures = newFeatureKeys - previousFeatureKeys
            val removedFeatures = previousFeatureKeys - newFeatureKeys
            features.addAll(addedFeatures.mapNotNull { SpeciesFeature.get(it)?.getDeclaredConstructor()?.newInstance() })
            features.removeAll { SpeciesFeature.getName(it) in removedFeatures }
            this.evolutionProxy.current().clear()
            updateAspects()
            updateForm()
            currentHealth = (hp * quotient).roundToInt()
            _species.emit(value)
        }
    var form = species.forms.first()
        set(value) {
            field = value
            // Evo proxy is already cleared on species update but the form may be changed by itself, this is fine and no unnecessary packets will be sent out
            this.evolutionProxy.current().clear()
            _form.emit(value)
        }
    var currentHealth = Int.MAX_VALUE
        set(value) {
            if(currentHealth <= 0 && value > 0) {
                this.healTimer = PokemonCobbled.config.healTimer
            }

            field = min(hp, value)
            _currentHealth.emit(field)

            // If the Pokémon is fainted, give it a timer for it to wake back up
            if(this.isFainted()) {
                val faintTime = PokemonCobbled.config.defaultFaintTimer
                CobbledEvents.POKEMON_FAINTED.post(PokemonFaintedEvent(this, faintTime)) {
                    this.faintedTimer = it.faintedTimer
                }
            }
        }
    var gender = Gender.GENDERLESS
        set(value) {
            if (!isClient) {
                if (species.maleRatio == null && value != Gender.GENDERLESS) {
                    return
                } else if (species.maleRatio != null && value == Gender.GENDERLESS) {
                    return
                } else if (species.maleRatio == 0F && value == Gender.MALE) {
                    return
                } else if (species.maleRatio == 1F && value == Gender.FEMALE) {
                    return
                }
            }
            field = value
            updateAspects()
            _gender.emit(value)
        }
    var status: PersistentStatusContainer? = null
        set(value) {
            field = value
            this._status.emit(value?.status?.name?.toString() ?: "")
        }
    var level = 1
        set(value) {
            if (value < 1) {
                throw IllegalArgumentException("Level cannot be negative")
            }

            val hpRatio = (currentHealth / hp.toFloat()).coerceIn(0F, 1F)
            /*
             * When people set the level programmatically the experience value will become incorrect.
             * Specifically check for when there's a mismatch and update the experience.
             */
            field = value
            if (experienceGroup.getLevel(experience) != value) {
                experience = experienceGroup.getExperience(value)
            }
            _level.emit(value)

            currentHealth = ceil(hpRatio * hp).coerceIn(0..hp)
        }
    var experience = 0
        protected set(value) {
            field = value
            _experience.emit(value)
        }
    var friendship = 0
        set(value) { field = value ; _friendship.emit(value) }
    var state: PokemonState = InactivePokemonState()
        set(value) {
            if (field is ActivePokemonState && !isClient) {
                (field as ActivePokemonState).recall()
            }
            field = value
            _state.emit(value)
        }

    val entity: PokemonEntity?
        get() = state.let { if (it is ActivePokemonState) it.entity else null }

    val primaryType: ElementalType
        get() = form.primaryType

    val secondaryType: ElementalType?
        get() = form.secondaryType

    val types: Iterable<ElementalType>
        get() = form.types

    var shiny = false
        set(value) {
            field = value
            updateAspects()
            _shiny.emit(value)
        }

    var nature = Natures.getRandomNature()
        set(value) { field = value ; _nature.emit(value.name.toString()) }
    var mintedNature: Nature? = null
        set(value) { field = value ; _mintedNature.emit(value?.name?.toString() ?: "") }

    val moveSet = MoveSet()

    val experienceGroup: ExperienceGroup
        get() = form.experienceGroup

    var faintedTimer: Int = -1
        set(value) {
            field = value
        }

    var healTimer: Int = -1
        set(value) {
            field = value
        }

    /**
     * All moves that the Pokémon has, at some point, known. This is to allow players to
     * swap in moves they've used before at any time, while holding onto the remaining PP
     * that they had last.
     */
    val benchedMoves = BenchedMoves()

    var ability: Ability = form.standardAbilities.random().create()

    val hp: Int
        get() = getStat(Stats.HP)
    val attack: Int
        get() = getStat(Stats.ATTACK)
    val defence: Int
        get() = getStat(Stats.DEFENCE)
    val specialAttack: Int
        get() = getStat(Stats.SPECIAL_ATTACK)
    val specialDefence: Int
        get() = getStat(Stats.SPECIAL_DEFENCE)
    val speed: Int
        get() = getStat(Stats.SPEED)

    var ivs = IVs.createRandomIVs()
    var evs = EVs.createEmpty()
    var scaleModifier = 1F

    var caughtBall: PokeBall = PokeBalls.POKE_BALL
        set(value) { field = value ; _caughtBall.emit(caughtBall.name.toString()) }
    var features = mutableListOf<SpeciesFeature>()
    var aspects = setOf<String>()
        set(value) {
            if (field != value) {
                field = value
                if (!isClient) {
                    updateForm()
                }
                _aspects.emit(value)
            }
        }

    private var isClient = false
    val storeCoordinates = SettableObservable<StoreCoordinates<*>?>(null)

    // We want non-optional evolutions to trigger first to avoid unnecessary packets and any cost associate with an optional one that would just be lost
    val evolutions: Iterable<Evolution> get() = this.form.evolutions.sortedBy { evolution -> evolution.optional }

    val preEvolution: PreEvolution? get() = this.form.preEvolution

    // Lazy due to leaking this
    /**
     * Provides the sided [EvolutionController]s, these operations can be done safely with a simple side check.
     * This can be done beforehand or using [EvolutionProxy.isClient].
     */
    val evolutionProxy: EvolutionProxy<EvolutionDisplay, Evolution> by lazy { CobbledEvolutionProxy(this, this.isClient) }

    open fun getStat(stat: Stat): Int {
        return if (stat == Stats.HP) {
            if (species.name == "shedinja") {
                1
            } else {
                (2 * form.baseStats[Stats.HP]!! + ivs[Stats.HP]!! + (evs[Stats.HP]!! / 4)) * level / 100 + level + 10
            }
        } else {
            nature.modifyStat(stat, (2 * (form.baseStats[stat] ?: 1) * ivs.getOrOne(stat) + evs.getOrOne(stat) / 4) / 100 * level + 5)
        }
    }

    fun sendOut(level: ServerWorld, position: Vec3d, mutation: (PokemonEntity) -> Unit = {}): PokemonEntity {
        val entity = PokemonEntity(level, this)
        entity.setPosition(position)
        mutation(entity)
        level.spawnEntity(entity)
        state = SentOutState(entity)
        return entity
    }

    fun recall() {
        this.state = InactivePokemonState()
    }

    fun heal() {
        this.currentHealth = hp
        this.moveSet.heal()
        this.status = null
        this.faintedTimer = -1
        this.healTimer = -1
    }

    fun isFainted(): Boolean {
        return currentHealth <= 0
    }

    fun applyStatus(status: PersistentStatus) {
        this.status = PersistentStatusContainer(status, status.statusPeriod().random())
        if (this.status != null) {
            this._status.emit(this.status!!.status.name.toString())
        }
    }

    fun saveToNBT(nbt: NbtCompound): NbtCompound {
        nbt.putUuid(DataKeys.POKEMON_UUID, uuid)
        nbt.putShort(DataKeys.POKEMON_SPECIES_DEX, species.nationalPokedexNumber.toShort())
        nbt.putString(DataKeys.POKEMON_FORM_ID, form.name)
        nbt.putInt(DataKeys.POKEMON_EXPERIENCE, experience)
        nbt.putShort(DataKeys.POKEMON_LEVEL, level.toShort())
        nbt.putShort(DataKeys.POKEMON_FRIENDSHIP, friendship.toShort())

        nbt.putShort(DataKeys.POKEMON_HEALTH, currentHealth.toShort())
        nbt.put(DataKeys.POKEMON_IVS, ivs.saveToNBT(NbtCompound()))
        nbt.put(DataKeys.POKEMON_EVS, evs.saveToNBT(NbtCompound()))
        nbt.put(DataKeys.POKEMON_MOVESET, moveSet.getNBT())
        nbt.putFloat(DataKeys.POKEMON_SCALE_MODIFIER, scaleModifier)
        nbt.putBoolean(DataKeys.POKEMON_SHINY, shiny)
        val abilityNBT = ability.saveToNBT(NbtCompound())
        nbt.put(DataKeys.POKEMON_ABILITY, abilityNBT)
        state.writeToNBT(NbtCompound())?.let { nbt.put(DataKeys.POKEMON_STATE, it) }
        status?.saveToNBT(NbtCompound())?.let { nbt.put(DataKeys.POKEMON_STATUS, it) }
        nbt.putString(DataKeys.POKEMON_CAUGHT_BALL, caughtBall.name.toString())
        nbt.putInt(DataKeys.POKEMON_FAINTED_TIMER, faintedTimer)
        nbt.putInt(DataKeys.POKEMON_HEALING_TIMER, healTimer)
        nbt.put(DataKeys.BENCHED_MOVES, benchedMoves.saveToNBT(NbtList()))
        nbt.put(DataKeys.POKEMON_EVOLUTIONS, this.evolutionProxy.saveToNBT())
        return nbt
    }

    fun loadFromNBT(nbt: NbtCompound): Pokemon {
        uuid = nbt.getUuid(DataKeys.POKEMON_UUID)
        species = PokemonSpecies.getByPokedexNumber(nbt.getInt(DataKeys.POKEMON_SPECIES_DEX))
            ?: throw IllegalStateException("Tried to read a species with national PokéDex number ${nbt.getInt(DataKeys.POKEMON_SPECIES_DEX)}")
        form = species.forms.find { it.name == nbt.getString(DataKeys.POKEMON_FORM_ID) } ?: species.forms.first()
        experience = nbt.getInt(DataKeys.POKEMON_EXPERIENCE)
        level = nbt.getShort(DataKeys.POKEMON_LEVEL).toInt()
        friendship = nbt.getShort(DataKeys.POKEMON_FRIENDSHIP).toInt()
        currentHealth = nbt.getShort(DataKeys.POKEMON_HEALTH).toInt()
        ivs.loadFromNBT(nbt.getCompound(DataKeys.POKEMON_IVS))
        evs.loadFromNBT(nbt.getCompound(DataKeys.POKEMON_EVS))
        moveSet.loadFromNBT(nbt)
        scaleModifier = nbt.getFloat(DataKeys.POKEMON_SCALE_MODIFIER)
        val abilityNBT = nbt.getCompound(DataKeys.POKEMON_ABILITY) ?: NbtCompound()
        val abilityName = abilityNBT.getString(DataKeys.POKEMON_ABILITY_NAME).takeIf { it.isNotEmpty() } ?: "drought"
        ability = Abilities.getOrException(abilityName).create(abilityNBT)
        shiny = nbt.getBoolean(DataKeys.POKEMON_SHINY)
        if (nbt.contains(DataKeys.POKEMON_STATE)) {
            val stateNBT = nbt.getCompound(DataKeys.POKEMON_STATE)
            val type = stateNBT.getString(DataKeys.POKEMON_STATE_TYPE)
            val clazz = PokemonState.states[type]
            state = clazz?.getDeclaredConstructor()?.newInstance()?.readFromNBT(stateNBT) ?: InactivePokemonState()
        }
        if (nbt.contains(DataKeys.POKEMON_STATUS)) {
            val statusNBT = nbt.getCompound(DataKeys.POKEMON_STATUS)
            status = PersistentStatusContainer.loadFromNBT(statusNBT)
        }
        faintedTimer = nbt.getInt(DataKeys.POKEMON_FAINTED_TIMER)
        healTimer = nbt.getInt(DataKeys.POKEMON_HEALING_TIMER)
        val ballName = nbt.getString(DataKeys.POKEMON_CAUGHT_BALL)
        caughtBall = PokeBalls.getPokeBall(Identifier(ballName)) ?: PokeBalls.POKE_BALL
        benchedMoves.loadFromNBT(nbt.getList(DataKeys.BENCHED_MOVES, COMPOUND_TYPE.toInt()))
        nbt.get(DataKeys.POKEMON_EVOLUTIONS)?.let { tag -> this.evolutionProxy.loadFromNBT(tag) }
        return this
    }

    fun saveToJSON(json: JsonObject): JsonObject {
        json.addProperty(DataKeys.POKEMON_UUID, uuid.toString())
        json.addProperty(DataKeys.POKEMON_SPECIES_DEX, species.nationalPokedexNumber)
        json.addProperty(DataKeys.POKEMON_FORM_ID, form.name)
        json.addProperty(DataKeys.POKEMON_EXPERIENCE, experience)
        json.addProperty(DataKeys.POKEMON_LEVEL, level)
        json.addProperty(DataKeys.POKEMON_FRIENDSHIP, friendship)
        json.addProperty(DataKeys.POKEMON_HEALTH, currentHealth)
        json.add(DataKeys.POKEMON_IVS, ivs.saveToJSON(JsonObject()))
        json.add(DataKeys.POKEMON_EVS, evs.saveToJSON(JsonObject()))
        json.add(DataKeys.POKEMON_MOVESET, moveSet.saveToJSON(JsonObject()))
        json.addProperty(DataKeys.POKEMON_SCALE_MODIFIER, scaleModifier)
        json.add(DataKeys.POKEMON_ABILITY, ability.saveToJSON(JsonObject()))
        json.addProperty(DataKeys.POKEMON_SHINY, shiny)
        state.writeToJSON(JsonObject())?.let { json.add(DataKeys.POKEMON_STATE, it) }
        status?.saveToJSON(JsonObject())?.let { json.add(DataKeys.POKEMON_STATUS, it) }
        json.addProperty(DataKeys.POKEMON_CAUGHT_BALL, caughtBall.name.toString())
        json.add(DataKeys.BENCHED_MOVES, benchedMoves.saveToJSON(JsonArray()))
        json.addProperty(DataKeys.POKEMON_FAINTED_TIMER, faintedTimer)
        json.addProperty(DataKeys.POKEMON_HEALING_TIMER, healTimer)
        json.add(DataKeys.POKEMON_EVOLUTIONS, this.evolutionProxy.saveToJson())
        return json
    }

    fun loadFromJSON(json: JsonObject): Pokemon {
        uuid = UUID.fromString(json.get(DataKeys.POKEMON_UUID).asString)
        species = PokemonSpecies.getByPokedexNumber(json.get(DataKeys.POKEMON_SPECIES_DEX).asInt)
            ?: throw IllegalStateException("Tried to read a species with national pokedex number ${json.get(DataKeys.POKEMON_SPECIES_DEX).asInt}")
        form = species.forms.find { it.name == json.get(DataKeys.POKEMON_FORM_ID).asString } ?: species.forms.first()
        experience = json.get(DataKeys.POKEMON_EXPERIENCE).asInt
        level = json.get(DataKeys.POKEMON_LEVEL).asInt
        friendship = json.get(DataKeys.POKEMON_FRIENDSHIP).asInt
        currentHealth = json.get(DataKeys.POKEMON_HEALTH).asInt
        ivs.loadFromJSON(json.getAsJsonObject(DataKeys.POKEMON_IVS))
        evs.loadFromJSON(json.getAsJsonObject(DataKeys.POKEMON_EVS))
        moveSet.loadFromJSON(json.get(DataKeys.POKEMON_MOVESET).asJsonObject)
        scaleModifier = json.get(DataKeys.POKEMON_SCALE_MODIFIER).asFloat
        val abilityJSON = json.get(DataKeys.POKEMON_ABILITY)?.asJsonObject ?: JsonObject()
        ability = Abilities.getOrException(abilityJSON.get(DataKeys.POKEMON_ABILITY_NAME)?.asString ?: "drought").create(abilityJSON)
        shiny = json.get(DataKeys.POKEMON_SHINY).asBoolean
        if (json.has(DataKeys.POKEMON_STATE)) {
            val stateJson = json.get(DataKeys.POKEMON_STATE).asJsonObject
            val type = stateJson.get(DataKeys.POKEMON_STATE_TYPE)?.asString
            val clazz = type?.let { PokemonState.states[it] }
            state = clazz?.getDeclaredConstructor()?.newInstance()?.readFromJSON(stateJson) ?: InactivePokemonState()
        }
        if (json.has(DataKeys.POKEMON_STATUS)) {
            val statusJson = json.get(DataKeys.POKEMON_STATUS).asJsonObject
            status = PersistentStatusContainer.loadFromJSON(statusJson)
        }
        val ballName = json.get(DataKeys.POKEMON_CAUGHT_BALL).asString
        caughtBall = PokeBalls.getPokeBall(Identifier(ballName)) ?: PokeBalls.POKE_BALL
        benchedMoves.loadFromJSON(json.get(DataKeys.BENCHED_MOVES)?.asJsonArray ?: JsonArray())
        faintedTimer = json.get(DataKeys.POKEMON_FAINTED_TIMER).asInt
        healTimer = json.get(DataKeys.POKEMON_HEALING_TIMER).asInt
        this.evolutionProxy.loadFromJson(json.get(DataKeys.POKEMON_EVOLUTIONS))
        return this
    }

    fun saveToBuffer(buffer: PacketByteBuf, toClient: Boolean): PacketByteBuf {
        buffer.writeBoolean(toClient)
        buffer.writeUuid(uuid)
        buffer.writeShort(species.nationalPokedexNumber)
        buffer.writeString(form.name)
        buffer.writeInt(experience)
        buffer.writeByte(level)
        buffer.writeShort(friendship)
        buffer.writeShort(currentHealth)
        ivs.saveToBuffer(buffer)
        evs.saveToBuffer(buffer)
        moveSet.saveToBuffer(buffer)
        buffer.writeFloat(scaleModifier)
        buffer.writeString(ability.name)
        buffer.writeBoolean(shiny)
        state.writeToBuffer(buffer)
        buffer.writeString(status?.status?.name?.toString() ?: "")
        buffer.writeString(caughtBall.name.toString())
        benchedMoves.saveToBuffer(buffer)
        buffer.writeInt(faintedTimer)
        buffer.writeInt(healTimer)
        buffer.writeSizedInt(IntSize.U_BYTE, aspects.size)
        aspects.forEach { buffer.writeString(it) }
        this.evolutionProxy.saveToBuffer(buffer, toClient)
        return buffer
    }

    fun loadFromBuffer(buffer: PacketByteBuf): Pokemon {
        isClient = buffer.readBoolean()
        uuid = buffer.readUuid()
        species = PokemonSpecies.getByPokedexNumber(buffer.readUnsignedShort())
            ?: throw IllegalStateException("Pokemon#loadFromBuffer cannot find the species! Species reference data has not been synchronized correctly!")
        val formId = buffer.readString()
        form = species.forms.find { it.name == formId } ?: species.forms.first()
        experience = buffer.readInt()
        level = buffer.readUnsignedByte().toInt()
        friendship = buffer.readUnsignedShort()
        currentHealth = buffer.readUnsignedShort()
        ivs.loadFromBuffer(buffer)
        evs.loadFromBuffer(buffer)
        moveSet.loadFromBuffer(buffer)
        scaleModifier = buffer.readFloat()
        ability = Abilities.getOrException(buffer.readString()).create()
        shiny = buffer.readBoolean()
        state = PokemonState.fromBuffer(buffer)
        val status = Statuses.getStatus(Identifier(buffer.readString()))
        if (status != null && status is PersistentStatus) {
            this.status = PersistentStatusContainer(status, 0)
        }
        val ballName = buffer.readString()
        caughtBall = PokeBalls.getPokeBall(Identifier(ballName)) ?: PokeBalls.POKE_BALL
        benchedMoves.loadFromBuffer(buffer)
        faintedTimer = buffer.readInt()
        healTimer = buffer.readInt()
        val aspects = mutableSetOf<String>()
        repeat(times = buffer.readSizedInt(IntSize.U_BYTE)) {
            aspects.add(buffer.readString())
        }
        this.aspects = aspects
        this.evolutionProxy.loadFromBuffer(buffer)
        return this
    }

    fun clone(useJSON: Boolean = true, newUUID: Boolean = true): Pokemon {
        val pokemon = if (useJSON) {
            Pokemon().loadFromJSON(saveToJSON(JsonObject()))
        } else {
            Pokemon().loadFromNBT(saveToNBT(NbtCompound()))
        }
        if (newUUID) {
            pokemon.uuid = UUID.randomUUID()
        }
        return pokemon
    }

    fun getOwnerPlayer() : ServerPlayerEntity? {
        storeCoordinates.get().let {
            if (isPlayerOwned()) {
                return getServer()?.playerManager?.getPlayer(it!!.store.uuid)
            }
        }
        return null
    }

    fun getOwnerUUID() : UUID? {
        storeCoordinates.get().let {
            if (isPlayerOwned()) return it!!.store.uuid;
        }
        return null
    }

    fun belongsTo(player: PlayerEntity) = storeCoordinates.get()?.let { it.store.uuid == player.uuid } == true
    fun isPlayerOwned() = storeCoordinates.get()?.let { it.store is PlayerPartyStore /* || it.store is PCStore */ } == true
    fun isWild() = storeCoordinates.get() == null

    private fun validFriendship (value : Int) : Boolean {
        return value in 0..255
    }

    fun setFriendship (amount : Int) : Boolean {
        if (validFriendship(amount)) friendship = amount
        return friendship == amount
    }

    fun incrementFriendship (amount : Int) : Boolean {
        val value = friendship + amount
        if (validFriendship(value)) friendship = value
        return friendship == value
    }

    fun decrementFriendship (amount : Int) : Boolean {
        val value = friendship - amount
        if (validFriendship(value)) friendship = value
        return friendship == value
    }

    val allAccessibleMoves: Set<MoveTemplate>
        get() = form.levelUpMoves.getMovesUpTo(level) + benchedMoves.map { it.moveTemplate }

    fun updateAspects() {
        /*
         * We don't want to run this for client representations of Pokémon as they won't always have the same
         * aspect providers, and we want the server side to entirely manage them anyway.
         */
        if (!isClient) {
            aspects = AspectProvider.providers.flatMap { it.provide(this) }.toSet()
        }
    }

    fun updateForm() {
        val newForm = species.getForm(aspects) ?: run {
            LOGGER.error("Unable to find a suitable form for a ${species.name} with aspects: ${aspects.joinToString()}!")
            species.forms.first()
        }
        if (form != newForm) {
            // Form updated!
            form = newForm
        }
    }

    // TODO a check function for gender to make sure a changed species hasn't broken the gender of the pokemon, and fix

    fun initialize(): Pokemon {
        // TODO some other initializations to do with form n shit
        gender = if (species.maleRatio == null || species.maleRatio!! !in 0F..1F) {
            Gender.GENDERLESS
        } else if (species.maleRatio!! == 1F || Random.nextFloat() <= species.maleRatio!!) {
            Gender.MALE
        } else {
            Gender.FEMALE
        }
        // shiny = randomize, probably
        initializeMoveset()
        initializeSpeciesFeatures()
        return this
    }

    fun initializeSpeciesFeatures() {
        features.clear()
        features.addAll(species.features.mapNotNull { SpeciesFeature.get(it)?.getDeclaredConstructor()?.newInstance() })
    }

    fun initializeMoveset(preferLatest: Boolean = true) {
        val possibleMoves = form.levelUpMoves.getMovesUpTo(level).toMutableList()

        moveSet.doWithoutEmitting {
            moveSet.clear()
            if (possibleMoves.isEmpty()) {
                moveSet.add(Moves.getExceptional().create())
                return@doWithoutEmitting
            }

            val selector: () -> MoveTemplate? = {
                if (preferLatest) {
                    possibleMoves.removeLastOrNull()
                } else {
                    val random = possibleMoves.randomOrNull()
                    if (random != null) {
                        possibleMoves.remove(random)
                    }
                    random
                }
            }

            for (i in 0 until 4) {
                val move = selector() ?: break
                moveSet.setMove(i, move.create())
            }
        }
        moveSet.update()
    }

    fun getExperienceToNextLevel() = getExperienceToLevel(level + 1)
    fun getExperienceToLevel(level: Int): Int {
        return if (level <= this.level) {
            0
        } else {
            experienceGroup.getExperience(level) - experience
        }
    }

    fun setExperienceAndUpdateLevel(xp: Int) {
        experience = xp
        val newLevel = experienceGroup.getLevel(xp)
        if (newLevel != level) {
            level = newLevel
        }
    }

    fun addExperienceWithPlayer(player: ServerPlayerEntity, xp: Int) {
        player.sendServerMessage(lang("experience.gained", species.translatedName, xp))
        val result = addExperience(xp)
        if (result.oldLevel != result.newLevel) {
            player.sendServerMessage(lang("experience.level_up", species.translatedName, result.newLevel))
            result.newMoves.forEach {
                player.sendServerMessage(lang("experience.learned_move", species.translatedName, it.displayName))
            }
        }
    }

    fun <T : SpeciesFeature> getFeature(name: String) = features.find { SpeciesFeature.getName(it) == name } as? T

    /**
     * Copies the specified properties from this Pokémon into a new [PokemonProperties] instance.
     *
     * You can find a bunch of built-in extractors inside [PokemonPropertyExtractor] statically.
     */
    fun createPokemonProperties(vararg extractors: PokemonPropertyExtractor): PokemonProperties {
        val properties = PokemonProperties()
        extractors.forEach { it(this, properties) }
        return properties
    }

    fun addExperience(xp: Int): AddExperienceResult {
        if (xp < 0) {
            return AddExperienceResult(level, level, emptySet()) // no negatives!
        }

        val oldLevel = level
        val previousLevelUpMoves = form.levelUpMoves.getMovesUpTo(oldLevel)
        // TODO xp gain event
        experience += xp
        val newLevel = experienceGroup.getLevel(experience)
        if (newLevel != oldLevel) {
            // TODO level up event?
            level = newLevel
        }

        val newLevelUpMoves = form.levelUpMoves.getMovesUpTo(newLevel)
        val differences = newLevelUpMoves - previousLevelUpMoves
        differences.forEach {
            if (moveSet.hasSpace()) {
                moveSet.add(it.create())
            }
        }

        return AddExperienceResult(oldLevel, newLevel, differences)
    }

    fun levelUp() = addExperience(getExperienceToNextLevel())

    /**
     * Exchanges an existing move set move with a benched or otherwise accessible move that is not in the move set.
     *
     * PP is transferred onto the new move using the % of PP that the original move had and applying it to the new one.
     *
     * @return true if it succeeded, false if it failed to exchange the moves. Failure can occur if the oldMove is not
     * a move set move.
     */
    fun exchangeMove(oldMove: MoveTemplate, newMove: MoveTemplate): Boolean {
        val benchedNewMove = benchedMoves.find { it.moveTemplate == newMove } ?: BenchedMove(newMove, 0)

        if (moveSet.hasSpace()) {
            benchedMoves.remove(newMove)
            val move = newMove.create()
            move.raisedPpStages = benchedNewMove.ppRaisedStages
            move.currentPp = move.maxPp
            moveSet.add(move)
            return true
        }

        val currentMove = moveSet.find { it.template == oldMove } ?: return false
        val currentPPRatio = currentMove.let { it.currentPp / it.maxPp.toFloat() }
        benchedMoves.doThenEmit {
            benchedMoves.remove(newMove)
            benchedMoves.add(BenchedMove(currentMove.template, currentMove.raisedPpStages))
        }

        val move = newMove.create()
        move.raisedPpStages = benchedNewMove.ppRaisedStages
        move.currentPp = (currentPPRatio * move.maxPp).toInt()
        moveSet.setMove(moveSet.indexOf(currentMove), move)

        return true
    }

    fun notify(packet: PokemonUpdatePacket) {
        storeCoordinates.get()?.run { sendToPlayers(store.getObservingPlayers(), packet) }
    }

    fun <T> registerObservable(observable: SimpleObservable<T>, notifyPacket: ((T) -> PokemonUpdatePacket)? = null): SimpleObservable<T> {
        observables.add(observable)
        observable.subscribe {
            if (notifyPacket != null && storeCoordinates.get() != null) {
                notify(notifyPacket(it))
            }
            anyChangeObservable.emit(Unit)
        }
        return observable
    }

    private val observables = mutableListOf<Observable<*>>()
    private val anyChangeObservable = SimpleObservable<Unit>()

    fun getAllObservables() = observables.asIterable()
    /** Returns an [Observable] that emits Unit whenever any change is made to this Pokémon. The change itself is not included. */
    fun getChangeObservable(): Observable<Unit> = anyChangeObservable

    private val _form = SimpleObservable<FormData>()
    private val _species = registerObservable(SimpleObservable<Species>()) { SpeciesUpdatePacket(this, it) }
    private val _experience = registerObservable(SimpleObservable<Int>()) { ExperienceUpdatePacket(this, it) }
    private val _level = registerObservable(SimpleObservable<Int>()) { LevelUpdatePacket(this, it) }
    private val _friendship = registerObservable(SimpleObservable<Int>()) { FriendshipUpdatePacket(this, it) }
    private val _currentHealth = registerObservable(SimpleObservable<Int>()) { HealthUpdatePacket(this, it) }
    private val _shiny = registerObservable(SimpleObservable<Boolean>()) { ShinyUpdatePacket(this, it) }
    private val _nature = registerObservable(SimpleObservable<String>()) { NatureUpdatePacket(this, it, false) }
    private val _mintedNature = registerObservable(SimpleObservable<String>()) { NatureUpdatePacket(this, it, true) }
    private val _moveSet = registerObservable(moveSet.observable) { MoveSetUpdatePacket(this, moveSet) }
    private val _state = registerObservable(SimpleObservable<PokemonState>()) { PokemonStateUpdatePacket(this, it) }
    private val _status = registerObservable(SimpleObservable<String>()) { StatusUpdatePacket(this, it) }
    private val _caughtBall = registerObservable(SimpleObservable<String>()) { CaughtBallUpdatePacket(this, it) }
    private val _benchedMoves = registerObservable(benchedMoves.observable) { BenchedMovesUpdatePacket(this, it) }
    private val _ivs = registerObservable(ivs.observable) // TODO consider a packet for it for changed ivs
    private val _evs = registerObservable(evs.observable) // TODO needs a packet
    private val _aspects = registerObservable(SimpleObservable<Set<String>>()) { AspectsUpdatePacket(this, it) }
    private val _gender = registerObservable(SimpleObservable<Gender>()) { GenderUpdatePacket(this, it) }
}
