package com.cablemc.pokemoncobbled.common.pokemon

import com.cablemc.pokemoncobbled.common.api.abilities.Abilities
import com.cablemc.pokemoncobbled.common.api.abilities.Ability
import com.cablemc.pokemoncobbled.common.api.moves.MoveSet
import com.cablemc.pokemoncobbled.common.api.pokemon.Natures
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.api.pokemon.stats.Stats
import com.cablemc.pokemoncobbled.common.api.reactive.Observable
import com.cablemc.pokemoncobbled.common.api.reactive.SettableObservable
import com.cablemc.pokemoncobbled.common.api.reactive.SimpleObservable
import com.cablemc.pokemoncobbled.common.api.storage.StoreCoordinates
import com.cablemc.pokemoncobbled.common.api.storage.party.PlayerPartyStore
import com.cablemc.pokemoncobbled.common.api.types.ElementalType
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.net.PokemonCobbledNetwork.sendToPlayers
import com.cablemc.pokemoncobbled.common.net.messages.client.PokemonUpdatePacket
import com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update.LevelUpdatePacket
import com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update.MoveSetUpdatePacket
import com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update.NatureUpdatePacket
import com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update.PokemonStateUpdatePacket
import com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update.ShinyUpdatePacket
import com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update.SpeciesUpdatePacket
import com.cablemc.pokemoncobbled.common.pokemon.activestate.ActivePokemonState
import com.cablemc.pokemoncobbled.common.pokemon.activestate.InactivePokemonState
import com.cablemc.pokemoncobbled.common.pokemon.activestate.PokemonState
import com.cablemc.pokemoncobbled.common.pokemon.activestate.SentOutState
import com.cablemc.pokemoncobbled.common.util.DataKeys
import com.cablemc.pokemoncobbled.common.util.pokemonStatsOf
import com.cablemc.pokemoncobbled.common.util.readMapK
import com.cablemc.pokemoncobbled.common.util.writeMapK
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3
import java.util.UUID

class Pokemon {
    companion object {
        const val MAXIMUM_LEVEL = 100
    }

    var uuid: UUID = UUID.randomUUID()
    var species = PokemonSpecies.EEVEE
        set(value) { field = value ; _species.emit(value) }
    var form = species.forms.first()
        set(value) { field = value ; _form.emit(value) }
    var health = 20
        set(value) { field = value ; _health.emit(value) }
    var level = 5
        set(value) { field = value ; _level.emit(value) }
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

    var shiny = false
        set(value) { field = value ; _shiny.emit(value) }

    var nature = Natures.getRandomNature()
        set(value) { field = value ; _nature.emit(value.name.toString()) }
    var mintedNature: Nature? = null
        set(value) { field = value ; _mintedNature.emit(value?.name?.toString() ?: "") }

    var moveSet: MoveSet = MoveSet()
        set(value) { field = value ; _moveSet.emit(value) }

    var ability: Ability = form.standardAbilities.random().create()

    val stats = pokemonStatsOf(
        Stats.HP to 20,
        Stats.ATTACK to 10,
        Stats.DEFENCE to 10,
        Stats.SPECIAL_ATTACK to 10,
        Stats.SPECIAL_DEFENCE to 10,
        Stats.SPEED to 15
    )
    var ivs = IVs.createRandomIVs()
    var evs = EVs.createEmpty()
    var scaleModifier = 1f

    val storeCoordinates: SettableObservable<StoreCoordinates<*>?> = SettableObservable(null)

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

    val types = form.types

    fun saveToNBT(nbt: CompoundTag): CompoundTag {
        nbt.putUUID(DataKeys.POKEMON_UUID, uuid)
        nbt.putShort(DataKeys.POKEMON_SPECIES_DEX, species.nationalPokedexNumber.toShort())
        nbt.putString(DataKeys.POKEMON_FORM_ID, form.name)
        nbt.putShort(DataKeys.POKEMON_LEVEL, level.toShort())
        nbt.putShort(DataKeys.POKEMON_HEALTH, health.toShort())
        nbt.put(DataKeys.POKEMON_STATS, stats.saveToNBT(CompoundTag()))
        nbt.put(DataKeys.POKEMON_IVS, ivs.saveToNBT(CompoundTag()))
        nbt.put(DataKeys.POKEMON_EVS, evs.saveToNBT(CompoundTag()))
        nbt.put(DataKeys.POKEMON_MOVESET, moveSet.getNBT())
        nbt.putFloat(DataKeys.POKEMON_SCALE_MODIFIER, scaleModifier)
        nbt.putBoolean(DataKeys.POKEMON_SHINY, shiny)
        ability.saveToNBT(nbt)
        state.writeToNBT(CompoundTag())?.let { nbt.put(DataKeys.POKEMON_STATE, it) }
            return nbt
    }

    fun loadFromNBT(nbt: CompoundTag): Pokemon {
        uuid = nbt.getUUID(DataKeys.POKEMON_UUID)
        species = PokemonSpecies.getByPokedexNumber(nbt.getInt(DataKeys.POKEMON_SPECIES_DEX))
            ?: throw IllegalStateException("Tried to read a species with national PokéDex number ${nbt.getInt(DataKeys.POKEMON_SPECIES_DEX)}")
        form = species.forms.find { it.name == nbt.getString(DataKeys.POKEMON_FORM_ID) } ?: species.forms.first()
        level = nbt.getShort(DataKeys.POKEMON_LEVEL).toInt()
        health = nbt.getShort(DataKeys.POKEMON_HEALTH).toInt()
        stats.loadFromNBT(nbt.getCompound(DataKeys.POKEMON_STATS))
        ivs.loadFromNBT(nbt.getCompound(DataKeys.POKEMON_IVS))
        evs.loadFromNBT(nbt.getCompound(DataKeys.POKEMON_EVS))
        scaleModifier = nbt.getFloat(DataKeys.POKEMON_SCALE_MODIFIER)
        moveSet = MoveSet.loadFromNBT(nbt)
        nbt.putString(DataKeys.POKEMON_ABILITY_NAME, "drought")

        ability = Abilities.getOrException(nbt.getString(DataKeys.POKEMON_ABILITY_NAME)).create(nbt)
        shiny = nbt.getBoolean(DataKeys.POKEMON_SHINY)
        if (nbt.contains(DataKeys.POKEMON_STATE)) {
            val stateNBT = nbt.getCompound(DataKeys.POKEMON_STATE)
            val type = stateNBT.getString(DataKeys.POKEMON_STATE_TYPE)
            val clazz = PokemonState.states[type]
            state = clazz?.newInstance()?.readFromNBT(stateNBT) ?: InactivePokemonState()
        }
        return this
    }

    // TODO Ability, MoveSet
    fun saveToJSON(json: JsonObject): JsonObject {
        json.addProperty(DataKeys.POKEMON_UUID, uuid.toString())
        json.addProperty(DataKeys.POKEMON_SPECIES_DEX, species.nationalPokedexNumber)
        json.addProperty(DataKeys.POKEMON_FORM_ID, form.name)
        json.addProperty(DataKeys.POKEMON_LEVEL, level)
        json.addProperty(DataKeys.POKEMON_HEALTH, health)
        json.add(DataKeys.POKEMON_STATS, stats.saveToJSON(JsonObject()))
        json.add(DataKeys.POKEMON_IVS, ivs.saveToJSON(JsonObject()))
        json.add(DataKeys.POKEMON_EVS, evs.saveToJSON(JsonObject()))
        json.addProperty(DataKeys.POKEMON_SHINY, shiny)
        state.writeToJSON(JsonObject())?.let { json.add(DataKeys.POKEMON_STATE, it) }
        return json
    }

    // TODO Ability, MoveSet
    fun loadFromJSON(json: JsonObject): Pokemon {
        uuid = UUID.fromString(json.get(DataKeys.POKEMON_UUID).asString)
        species = PokemonSpecies.getByPokedexNumber(json.get(DataKeys.POKEMON_SPECIES_DEX).asInt)
            ?: throw IllegalStateException("Tried to read a species with national pokedex number ${json.get(DataKeys.POKEMON_SPECIES_DEX).asInt}")
        form = species.forms.find { it.name == json.get(DataKeys.POKEMON_FORM_ID).asString } ?: species.forms.first()
        level = json.get(DataKeys.POKEMON_LEVEL).asInt
        health = json.get(DataKeys.POKEMON_HEALTH).asInt
        stats.loadFromJSON(json.getAsJsonObject(DataKeys.POKEMON_STATS))
        ivs.loadFromJSON(json.getAsJsonObject(DataKeys.POKEMON_IVS))
        evs.loadFromJSON(json.getAsJsonObject(DataKeys.POKEMON_EVS))
        shiny = json.get(DataKeys.POKEMON_SHINY).asBoolean
        if (json.has(DataKeys.POKEMON_STATE)) {
            val stateJson = json.get(DataKeys.POKEMON_STATE).asJsonObject
            val type = stateJson.get(DataKeys.POKEMON_STATE_TYPE).asString
            val clazz = PokemonState.states[type]
            state = clazz?.newInstance()?.readFromJSON(stateJson) ?: InactivePokemonState()
        }
        return this
    }

    // TODO Ability
    fun saveToBuffer(buffer: FriendlyByteBuf): FriendlyByteBuf {
        buffer.writeUUID(uuid)
        buffer.writeShort(species.nationalPokedexNumber)
        buffer.writeUtf(form.name)
        buffer.writeByte(level)
        buffer.writeShort(health)
        buffer.writeMapK(map = stats) { (key, value) -> buffer.writeUtf(key.id) ; buffer.writeShort(value) }
        moveSet.saveToBuffer(buffer)
        buffer.writeMapK(map = evs) { (key, value) -> buffer.writeUtf(key.id) ; buffer.writeShort(value) }
        buffer.writeBoolean(shiny)
        state.writeToBuffer(buffer)
        return buffer
    }

    // TODO Ability
    fun loadFromBuffer(buffer: FriendlyByteBuf): Pokemon {
        uuid = buffer.readUUID()
        species = PokemonSpecies.getByPokedexNumber(buffer.readUnsignedShort())
            ?: throw IllegalStateException("Pokemon#loadFromBuffer cannot find the species! Species reference data has not been synchronized correct!")
        val formId = buffer.readUtf()
        form = species.forms.find { it.name == formId } ?: species.forms.first()
        level = buffer.readUnsignedByte().toInt()
        health = buffer.readUnsignedShort()
        // TODO throw exception or dummy stat?
        buffer.readMapK(map = stats) { Stats.getStat(buffer.readUtf())!! to buffer.readUnsignedShort() }
        moveSet = MoveSet.loadFromBuffer(buffer)
        // TODO throw exception or dummy stat?
        buffer.readMapK(map = evs) { Stats.getStat(buffer.readUtf())!! to buffer.readUnsignedShort() }
        shiny = buffer.readBoolean()
        state = PokemonState.fromBuffer(buffer)
        return this
    }

    fun belongsTo(player: Player) = storeCoordinates.get()?.let { it.store.uuid == player.uuid } == true
    fun isPlayerOwned() = storeCoordinates.get()?.let { it.store is PlayerPartyStore /* || it.store is PCStore */ } == true
    fun isWild() = storeCoordinates.get() == null

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
    private val _level = registerObservable(SimpleObservable<Int>()) { LevelUpdatePacket(this, it) }
    private val _health = SimpleObservable<Int>()
    private val _shiny = registerObservable(SimpleObservable<Boolean>()) { ShinyUpdatePacket(this, it) }
    private val _nature = registerObservable(SimpleObservable<String>()) { NatureUpdatePacket(this, it, false) }
    private val _mintedNature = registerObservable(SimpleObservable<String>()) { NatureUpdatePacket(this, it, true) }
    private val _moveSet = registerObservable(SimpleObservable<MoveSet>()) { MoveSetUpdatePacket(this, moveSet) }
    private val _state = registerObservable(SimpleObservable<PokemonState>()) { PokemonStateUpdatePacket(it) }

    fun getMoveSetObservable() = _moveSet

    fun getMaxHealth(): Int = (2 * stats[Stats.HP]!! + ivs[Stats.HP]!! + (evs[Stats.HP]!! / 4) * level) / 100 + level + 10
}