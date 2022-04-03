package com.cablemc.pokemoncobbled.common.pokemon

import com.cablemc.pokemoncobbled.common.CobbledNetwork.sendToPlayers
import com.cablemc.pokemoncobbled.common.api.abilities.Abilities
import com.cablemc.pokemoncobbled.common.api.abilities.Ability
import com.cablemc.pokemoncobbled.common.api.moves.MoveSet
import com.cablemc.pokemoncobbled.common.api.moves.MoveTemplate
import com.cablemc.pokemoncobbled.common.api.pokemon.experience.ExperienceGroup
import com.cablemc.pokemoncobbled.common.api.moves.Moves
import com.cablemc.pokemoncobbled.common.api.pokemon.Natures
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.api.pokemon.stats.Stats
import com.cablemc.pokemoncobbled.common.pokemon.status.PersistentStatusContainer
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
import com.cablemc.pokemoncobbled.common.pokemon.activestate.ActivePokemonState
import com.cablemc.pokemoncobbled.common.pokemon.activestate.InactivePokemonState
import com.cablemc.pokemoncobbled.common.pokemon.activestate.PokemonState
import com.cablemc.pokemoncobbled.common.pokemon.activestate.SentOutState
import com.cablemc.pokemoncobbled.common.pokemon.stats.Stat
import com.cablemc.pokemoncobbled.common.pokemon.status.PersistentStatus
import com.cablemc.pokemoncobbled.common.util.DataKeys
import com.cablemc.pokemoncobbled.common.util.getServer
import com.cablemc.pokemoncobbled.common.util.lang
import com.cablemc.pokemoncobbled.common.util.readMapK
import com.cablemc.pokemoncobbled.common.util.sendServerMessage
import com.cablemc.pokemoncobbled.common.util.writeMapK
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3
import java.lang.Integer.min
import java.util.UUID

open class Pokemon {
    var uuid: UUID = UUID.randomUUID()
    var species = PokemonSpecies.EEVEE
        set(value) {
            val quotient = currentHealth / hp
            field = value
            currentHealth = quotient * hp
            _species.emit(value)
        }
    var form = species.forms.first()
        set(value) { field = value ; _form.emit(value) }
    var currentHealth = Int.MAX_VALUE
        set(value) {
            field = min(hp, value)
            _currentHealth.emit(field)
        }
    var status: PersistentStatusContainer? = null
        private set
    var level = 1
        set(value) {
            if (value < 1) {
                throw IllegalArgumentException("Level cannot be negative")
            }
            /*
             * When people set the level programmatically the experience value will become incorrect.
             * Specifically check for when there's a mismatch and update the experience.
             */
            field = value
            if (experienceGroup.getLevel(experience) != value) {
                experience = experienceGroup.getExperience(value)
            }
            _level.emit(value)
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
            if (field is ActivePokemonState) {
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
        set(value) { field = value ; _shiny.emit(value) }

    var nature = Natures.getRandomNature()
        set(value) { field = value ; _nature.emit(value.name.toString()) }
    var mintedNature: Nature? = null
        set(value) { field = value ; _mintedNature.emit(value?.name?.toString() ?: "") }

    var moveSet: MoveSet = MoveSet()
        set(value) { field = value ; _moveSet.emit(value) }

    val experienceGroup: ExperienceGroup
        get() = form.experienceGroup

    /** All the not-level-up moves that the Pokémon has learned over its lifetime. */
    val learnedMoves = mutableListOf<MoveTemplate>()

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

    val storeCoordinates = SettableObservable<StoreCoordinates<*>?>(null)

    open fun getStat(stat: Stat): Int {
        return if (stat == Stats.HP) {
            // TODO shedinja should just return 1
            (2 * form.baseStats[Stats.HP]!! + ivs[Stats.HP]!! + (evs[Stats.HP]!! / 4) * level) / 100 + level + 10
        } else {
            nature.modifyStat(stat, (2 * form.baseStats.getOrOne(stat) * ivs.getOrOne(stat) + evs.getOrOne(stat) / 4) / 100 * level + 5)
        }
    }

    fun sendOut(level: ServerLevel, position: Vec3, mutation: (PokemonEntity) -> Unit = {}): PokemonEntity {
        val entity = PokemonEntity(level, this)
        entity.setPos(position)
        mutation(entity)
        level.addFreshEntity(entity)
        state = SentOutState(entity)
        return entity
    }

    fun recall() {
        this.state = InactivePokemonState()
    }

    fun heal() {
        this.currentHealth = hp
    }

    fun applyStatus(status: PersistentStatus) {
        this.status = PersistentStatusContainer(status)
        if(this.status != null) {
            this._status.emit(this.status!!.status.name.toString())
        }
    }

    fun saveToNBT(nbt: CompoundTag): CompoundTag {
        nbt.putUUID(DataKeys.POKEMON_UUID, uuid)
        nbt.putShort(DataKeys.POKEMON_SPECIES_DEX, species.nationalPokedexNumber.toShort())
        nbt.putString(DataKeys.POKEMON_FORM_ID, form.name)
        nbt.putInt(DataKeys.POKEMON_EXPERIENCE, experience)
        nbt.putShort(DataKeys.POKEMON_LEVEL, level.toShort())
        nbt.putShort(DataKeys.POKEMON_FRIENDSHIP, friendship.toShort())

        nbt.putShort(DataKeys.POKEMON_HEALTH, currentHealth.toShort())
        nbt.put(DataKeys.POKEMON_IVS, ivs.saveToNBT(CompoundTag()))
        nbt.put(DataKeys.POKEMON_EVS, evs.saveToNBT(CompoundTag()))
        nbt.put(DataKeys.POKEMON_MOVESET, moveSet.getNBT())
        nbt.putFloat(DataKeys.POKEMON_SCALE_MODIFIER, scaleModifier)
        nbt.putBoolean(DataKeys.POKEMON_SHINY, shiny)
        val abilityNBT = ability.saveToNBT(CompoundTag())
        nbt.put(DataKeys.POKEMON_ABILITY, abilityNBT)
        state.writeToNBT(CompoundTag())?.let { nbt.put(DataKeys.POKEMON_STATE, it) }
        status?.saveToNBT(CompoundTag())?.let { nbt.put(DataKeys.POKEMON_STATUS, it) }
        return nbt
    }

    fun loadFromNBT(nbt: CompoundTag): Pokemon {
        uuid = nbt.getUUID(DataKeys.POKEMON_UUID)
        species = PokemonSpecies.getByPokedexNumber(nbt.getInt(DataKeys.POKEMON_SPECIES_DEX))
            ?: throw IllegalStateException("Tried to read a species with national PokéDex number ${nbt.getInt(DataKeys.POKEMON_SPECIES_DEX)}")
        form = species.forms.find { it.name == nbt.getString(DataKeys.POKEMON_FORM_ID) } ?: species.forms.first()
        experience = nbt.getInt(DataKeys.POKEMON_EXPERIENCE)
        level = nbt.getShort(DataKeys.POKEMON_LEVEL).toInt()
        friendship = nbt.getShort(DataKeys.POKEMON_FRIENDSHIP).toInt()
        currentHealth = nbt.getShort(DataKeys.POKEMON_HEALTH).toInt()
        ivs.loadFromNBT(nbt.getCompound(DataKeys.POKEMON_IVS))
        evs.loadFromNBT(nbt.getCompound(DataKeys.POKEMON_EVS))
        moveSet = MoveSet.loadFromNBT(nbt)
        scaleModifier = nbt.getFloat(DataKeys.POKEMON_SCALE_MODIFIER)
        learnedMoves.clear()
        val abilityNBT = nbt.getCompound(DataKeys.POKEMON_ABILITY) ?: CompoundTag()
        val abilityName = abilityNBT.getString(DataKeys.POKEMON_ABILITY_NAME).takeIf { it.isNotEmpty() } ?: "drought"
        ability = Abilities.getOrException(abilityName).create(abilityNBT)
        shiny = nbt.getBoolean(DataKeys.POKEMON_SHINY)
        if (nbt.contains(DataKeys.POKEMON_STATE)) {
            val stateNBT = nbt.getCompound(DataKeys.POKEMON_STATE)
            val type = stateNBT.getString(DataKeys.POKEMON_STATE_TYPE)
            val clazz = PokemonState.states[type]
            state = clazz?.getDeclaredConstructor()?.newInstance()?.readFromNBT(stateNBT) ?: InactivePokemonState()
        }
        if(nbt.contains(DataKeys.POKEMON_STATUS)) {
            val statusNBT = nbt.getCompound(DataKeys.POKEMON_STATUS)
            status = PersistentStatusContainer.loadFromNBT(statusNBT)
        }
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
        moveSet = MoveSet.loadFromJSON(json.get(DataKeys.POKEMON_MOVESET).asJsonObject)
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
        if(json.has(DataKeys.POKEMON_STATUS)) {
            val statusJson = json.get(DataKeys.POKEMON_STATUS).asJsonObject
            status = PersistentStatusContainer.loadFromJSON(statusJson)
        }
        return this
    }

    fun saveToBuffer(buffer: FriendlyByteBuf): FriendlyByteBuf {
        buffer.writeUUID(uuid)
        buffer.writeShort(species.nationalPokedexNumber)
        buffer.writeUtf(form.name)
        buffer.writeInt(experience)
        buffer.writeByte(level)
        buffer.writeShort(friendship)
        buffer.writeShort(currentHealth)
        buffer.writeMapK(map = ivs, size = IntSize.U_BYTE) { (key, value) -> buffer.writeUtf(key.id) ; buffer.writeByte(value) }
        buffer.writeMapK(map = evs, size = IntSize.U_SHORT) { (key, value) -> buffer.writeUtf(key.id) ; buffer.writeShort(value) }
        moveSet.saveToBuffer(buffer)
        buffer.writeFloat(scaleModifier)
        buffer.writeUtf(ability.name)
        buffer.writeBoolean(shiny)
        state.writeToBuffer(buffer)
        buffer.writeUtf(status?.status?.name?.toString() ?: "")
        return buffer
    }

    fun loadFromBuffer(buffer: FriendlyByteBuf): Pokemon {
        uuid = buffer.readUUID()
        species = PokemonSpecies.getByPokedexNumber(buffer.readUnsignedShort())
            ?: throw IllegalStateException("Pokemon#loadFromBuffer cannot find the species! Species reference data has not been synchronized correctly!")
        val formId = buffer.readUtf()
        form = species.forms.find { it.name == formId } ?: species.forms.first()
        experience = buffer.readInt()
        level = buffer.readUnsignedByte().toInt()
        friendship = buffer.readUnsignedShort()
        currentHealth = buffer.readUnsignedShort()
        buffer.readMapK(map = ivs, size = IntSize.U_BYTE) { Stats.getStat(buffer.readUtf()) to buffer.readUnsignedByte().toInt() }
        buffer.readMapK(map = evs, size = IntSize.U_SHORT) { Stats.getStat(buffer.readUtf()) to buffer.readUnsignedShort() }
        moveSet = MoveSet.loadFromBuffer(buffer)
        scaleModifier = buffer.readFloat()
        ability = Abilities.getOrException(buffer.readUtf()).create()
        shiny = buffer.readBoolean()
        state = PokemonState.fromBuffer(buffer)
        val status = Statuses.getStatus(ResourceLocation(buffer.readUtf()))
        if(status != null && status is PersistentStatus) {
            this.status = PersistentStatusContainer(status, 0)
        }
        return this
    }

    fun clone(useJSON: Boolean = true, newUUID: Boolean = true): Pokemon {
        val pokemon = if (useJSON) {
            Pokemon().loadFromJSON(saveToJSON(JsonObject()))
        } else {
            Pokemon().loadFromNBT(saveToNBT(CompoundTag()))
        }
        if (newUUID) {
            pokemon.uuid = UUID.randomUUID()
        }
        return pokemon
    }

    fun getOwnerPlayer() : ServerPlayer? {
        storeCoordinates.get().let {
            if (isPlayerOwned()) {
                return getServer()?.playerList?.getPlayer(it!!.store.uuid)
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

    fun belongsTo(player: Player) = storeCoordinates.get()?.let { it.store.uuid == player.uuid } == true
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

    fun initialize() {

        // TODO some other initializations to do with form and gender n shit
        initializeMoveset()
    }

    fun initializeMoveset(preferLatest: Boolean = true) {
        val possibleMoves = form.levelUpMoves.getMovesUpTo(level).toMutableList()

        moveSet.clear()
        if (possibleMoves.isEmpty()) {
            moveSet.add(Moves.getExceptional().create())
            return
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

    fun addExperienceWithPlayer(player: ServerPlayer, xp: Int) {
        val previousLevel = level
        player.sendServerMessage(lang("experience.gained", species.translatedName, experience))
        addExperience(xp)
        if (previousLevel != level) {
            player.sendServerMessage(lang("experience.level_up", species.translatedName, level))
            // TODO level up moves, compare learn list from previousLevel to the new level
        }
    }

    fun addExperience(xp: Int) {
        if (xp < 0) {
            return // no negatives!
        }

        // TODO xp gain event
        experience += xp
        val newLevel = experienceGroup.getLevel(experience)
        if (newLevel != level) {
            // TODO level up event?
            level = newLevel
        }
    }

    fun levelUp() = addExperience(getExperienceToNextLevel())

    fun notify(packet: PokemonUpdatePacket) {
        storeCoordinates.get()?.run { sendToPlayers(store.getObservingPlayers(), packet) }
    }

    fun <T> registerObservable(observable: SimpleObservable<T>, notifyPacket: ((T) -> PokemonUpdatePacket)? = null): SimpleObservable<T> {
        observables.add(observable)
        if (notifyPacket != null) {
            observable.subscribe {
                storeCoordinates.get() ?: return@subscribe
                notify(notifyPacket(it))
            }
        }
        observable.subscribe { anyChangeObservable.emit(Unit) }
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
    private val _moveSet = registerObservable(SimpleObservable<MoveSet>()) { MoveSetUpdatePacket(this, moveSet) }
    private val _state = registerObservable(SimpleObservable<PokemonState>()) { PokemonStateUpdatePacket(it) }
    private val _status = registerObservable(SimpleObservable<String>()) { StatusUpdatePacket(this, it) }

    fun getMoveSetObservable() = _moveSet
}