package com.cablemc.pokemoncobbled.common.pokemon

import com.cablemc.pokemoncobbled.common.api.abilities.Abilities
import com.cablemc.pokemoncobbled.common.api.abilities.Ability
import com.cablemc.pokemoncobbled.common.api.moves.MoveSet
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
import com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update.SpeciesUpdatePacket
import com.cablemc.pokemoncobbled.common.util.DataKeys
import com.cablemc.pokemoncobbled.common.util.pokemonStatsOf
import com.cablemc.pokemoncobbled.common.util.readMapK
import com.cablemc.pokemoncobbled.common.util.writeMapK
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3
import java.util.UUID

class Pokemon {
    var uuid: UUID = UUID.randomUUID()
    var species = PokemonSpecies.EEVEE
        set(value) { field = value ; _species.emit(value) }
    var form = species.forms.first()
        set(value) { field = value ; _form.emit(value) }
    var health = 10
        set(value) { field = value ; _health.emit(value) }
    var level = 5
        set(value) { field = value ; _level.emit(value) }

    var entity: PokemonEntity? = null

    val primaryType: ElementalType
        get() = form.primaryType
    val secondaryType: ElementalType?
        get() = form.secondaryType

    var moveSet: MoveSet = MoveSet()

    var ability: Ability = form.standardAbilities.random().create()

    val stats = pokemonStatsOf(
        Stats.HP to 20,
        Stats.ATTACK to 10,
        Stats.DEFENCE to 10,
        Stats.SPECIAL_ATTACK to 10,
        Stats.SPECIAL_DEFENCE to 10,
        Stats.SPEED to 15
    )
    var scaleModifier = 1f

    val storeCoordinates: SettableObservable<StoreCoordinates<*>?> = SettableObservable(null)

    fun sendOut(level: ServerLevel, position: Vec3, mutation: (PokemonEntity) -> Unit = {}): PokemonEntity {
        val entity = PokemonEntity(level, this)
        entity.setPos(position)
        mutation(entity)
        level.addFreshEntity(entity)
        return entity
    }

    fun recall() {
        this.entity?.remove(Entity.RemovalReason.DISCARDED)
        this.entity = null
    }

    val types = form.types

    fun saveToNBT(nbt: CompoundTag): CompoundTag {
        nbt.putUUID(DataKeys.POKEMON_UUID, uuid)
        nbt.putShort(DataKeys.POKEMON_SPECIES_DEX, species.nationalPokedexNumber.toShort())
        nbt.putString(DataKeys.POKEMON_FORM_ID, form.name)
        nbt.putShort(DataKeys.POKEMON_LEVEL, level.toShort())
        nbt.putShort(DataKeys.POKEMON_HEALTH, health.toShort())
        nbt.put(DataKeys.POKEMON_STATS, stats.saveToNBT(CompoundTag()))
        nbt.put(DataKeys.POKEMON_MOVESET, moveSet.getNBT())
        nbt.putFloat(DataKeys.POKEMON_SCALE_MODIFIER, scaleModifier)
        ability.saveToNBT(nbt)
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
        scaleModifier = nbt.getFloat(DataKeys.POKEMON_SCALE_MODIFIER)
        moveSet = MoveSet.loadFromNBT(nbt)
        ability = Abilities.getOrException(nbt.getString(DataKeys.POKEMON_ABILITY_NAME)).create(nbt)
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
        return this
    }

    // TODO Ability, MoveSet - Last time I tries it errored :(
    fun saveToBuffer(buffer: FriendlyByteBuf): FriendlyByteBuf {
        buffer.writeUUID(uuid)
        buffer.writeShort(species.nationalPokedexNumber)
        buffer.writeUtf(form.name)
        buffer.writeByte(level)
        buffer.writeShort(health)
        buffer.writeMapK(map = stats) { (key, value) -> buffer.writeUtf(key.id) ; buffer.writeShort(value) }
        //moveSet.saveToBuffer(buffer)
        return buffer
    }

    // TODO Ability, MoveSet - Last time I tries it errored :(
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
        // This errors...
        //moveSet = MoveSet.loadFromBuffer(buffer)
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

    val ivHP = 1
    val evHP = 1

    fun getMaxHealth(): Int = (2 * stats[Stats.HP]!! + ivHP + (evHP / 4) * level) / 100 + level + 10
}