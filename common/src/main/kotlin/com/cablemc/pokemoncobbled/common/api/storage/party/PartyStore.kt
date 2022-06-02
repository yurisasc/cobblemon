package com.cablemc.pokemoncobbled.common.api.storage.party

import com.cablemc.pokemoncobbled.common.CobbledNetwork.sendPacket
import com.cablemc.pokemoncobbled.common.api.reactive.Observable
import com.cablemc.pokemoncobbled.common.api.reactive.Observable.Companion.emitWhile
import com.cablemc.pokemoncobbled.common.api.reactive.SimpleObservable
import com.cablemc.pokemoncobbled.common.api.storage.PokemonStore
import com.cablemc.pokemoncobbled.common.api.storage.StoreCoordinates
import com.cablemc.pokemoncobbled.common.battles.pokemon.BattlePokemon
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.InitializePartyPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.MovePartyPokemonPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.RemovePartyPokemonPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.SetPartyPokemonPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.SwapPartyPokemonPacket
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.DataKeys
import com.cablemc.pokemoncobbled.common.util.getServer
import com.google.gson.JsonObject
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity
import java.util.UUID

/**
 * A [PokemonStore] for a party of Pokémon. This is a simple structure that by default will hold 6 nullable slots of Pokémon.
 *
 * Please note that a party has no notion of a player, as this type of store could be used for trainers. For a party store
 * that knows about the player it is attached to, see [PlayerPartyStore].
 *
 * @author Hiroku
 * @since November 29th, 2021
 */
open class PartyStore(override val uuid: UUID) : PokemonStore<PartyPosition>() {
    protected val slots = MutableList<Pokemon?>(6) { null }
    protected val anyChangeObservable = SimpleObservable<Unit>()

    /** A list of player UUIDs representing players that are observing this store. This is NOT serialized/deserialized. */
    var observerUUIDs = mutableListOf<UUID>()

    override fun iterator() = slots.filterNotNull().iterator()
    override fun getAll() = slots.filterNotNull()

    /** Gets the Pokémon at the specified slot. It will return null if the slot is empty or the given slot is out of bounds. */
    fun get(slot: Int) = slot.takeIf { it < slots.size }?.let { slots[it] }
    override operator fun get(position: PartyPosition) = get(position.slot)

    /** Sets the Pokémon at the specified slot. */
    fun set(slot: Int, pokemon: Pokemon) = set(PartyPosition(slot), pokemon)
    override fun setAtPosition(position: PartyPosition, pokemon: Pokemon?) {
            if (position.slot >= slots.size) {
            throw IllegalArgumentException("Slot position is out of bounds")
        } else {
            slots[position.slot] = pokemon
            pokemon?.storeCoordinates?.set(StoreCoordinates(this, position))
            anyChangeObservable.emit(Unit)
        }
    }

    override fun getFirstAvailablePosition(): PartyPosition? {
        for (i in slots.indices) {
            if (slots[i] == null) {
                return PartyPosition(i)
            }
        }

        return null
    }

    override fun getObservingPlayers() = getServer()?.playerManager?.playerList?.filter { it.uuid in observerUUIDs } ?: emptyList()
    fun size() = slots.size

    override fun sendTo(player: ServerPlayerEntity) {
        player.sendPacket(InitializePartyPacket(false, uuid, slots.size))
        slots.forEachIndexed { index, pokemon ->
            if (pokemon != null) {
                player.sendPacket(SetPartyPokemonPacket(uuid, PartyPosition(index), pokemon))
            }
        }
    }

    override operator fun set(position: PartyPosition, pokemon: Pokemon) {
        super.set(position, pokemon)
        sendPacketToObservers(SetPartyPokemonPacket(uuid, position, pokemon))
    }

    override fun remove(pokemon: Pokemon): Boolean {
        return if (super.remove(pokemon)) {
            sendPacketToObservers(RemovePartyPokemonPacket(uuid, pokemon.uuid))
            true
        } else {
            false
        }
    }

    /** Swaps the contents of the two given slots. */
    fun swap(slot1: Int, slot2: Int) {
        if (slot1 !in slots.indices || slot2 !in slots.indices) {
            return
        }
        swap(PartyPosition(slot1), PartyPosition(slot2))
    }

    override fun swap(position1: PartyPosition, position2: PartyPosition) {
        val pokemon1 = get(position1)
        val pokemon2 = get(position2)
        super.swap(position1, position2)
        if (pokemon1 != null && pokemon2 != null) {
            sendPacketToObservers(SwapPartyPokemonPacket(uuid, pokemon1.uuid, pokemon2.uuid))
        } else if (pokemon1 != null || pokemon2 != null) {
            val newPosition = if (pokemon1 == null) position1 else position2
            val pokemon = pokemon1 ?: pokemon2!!
            sendPacketToObservers(MovePartyPokemonPacket(uuid, pokemon.uuid, newPosition))
        }
    }

    override fun initialize() {
        for (slot in slots.indices) {
            val pokemon = get(slot) ?: continue
            pokemon.storeCoordinates.set(StoreCoordinates(this, PartyPosition(slot)))
            pokemon.getChangeObservable()
                .pipe(emitWhile { pokemon.storeCoordinates.get()?.store == this })
                .subscribe { anyChangeObservable.emit(Unit) }
        }
    }

    override fun saveToNBT(nbt: NbtCompound): NbtCompound {
        nbt.putInt(DataKeys.STORE_SLOT_COUNT, slots.size)
        for (slot in slots.indices) {
            val pokemon = get(slot)
            if (pokemon != null) {
                nbt.put(DataKeys.STORE_SLOT + slot, pokemon.saveToNBT(NbtCompound()))
            }
        }
        return nbt
    }

    override fun loadFromNBT(nbt: NbtCompound): PartyStore {
        val slotCount = nbt.getInt(DataKeys.STORE_SLOT_COUNT)
        while (slotCount > slots.size) { slots.removeLast() }
        while (slotCount < slots.size) { slots.add(null) }
        for (slot in slots.indices) {
            val pokemonNBT = nbt.getCompound(DataKeys.STORE_SLOT + slot)
            if (!pokemonNBT.isEmpty) {
                slots[slot] = Pokemon().loadFromNBT(pokemonNBT)
            }
        }
        return this
    }

    override fun saveToJSON(json: JsonObject): JsonObject {
        json.addProperty(DataKeys.STORE_SLOT_COUNT, slots.size)
        for (slot in slots.indices) {
            val pokemon = get(slot)
            if (pokemon != null) {
                json.add(DataKeys.STORE_SLOT + slot, pokemon.saveToJSON(JsonObject()))
            }
        }
        return json
    }

    override fun loadFromJSON(json: JsonObject): PokemonStore<PartyPosition> {
        val slotCount = json.get(DataKeys.STORE_SLOT_COUNT).asInt
        while (slotCount > slots.size) { slots.removeLast() }
        while (slotCount < slots.size) { slots.add(null) }
        for (slot in slots.indices) {
            val key = DataKeys.STORE_SLOT + slot
            if (json.has(key)) {
                slots[slot] = Pokemon().loadFromJSON(json.get(key).asJsonObject)
            }
        }
        return this
    }

    override fun getAnyChangeObservable(): Observable<Unit> = anyChangeObservable

    fun heal() {
        forEach { it.heal() }
    }

    fun getHealingRemainderPercent(): Float {
        var totalPercent = 0.0f
        for (pokemon in this) {
            totalPercent += 1.0f - (pokemon.currentHealth / pokemon.hp)
        }
        return totalPercent
    }

    fun toBattleTeam(clone: Boolean = false, checkHealth: Boolean = true, leadingPokemon: UUID? = null) = mapNotNull {
        // TODO Other 'able to battle' checks
        if (checkHealth && it.currentHealth <= 0) {
            return@mapNotNull null
        }
        return@mapNotNull if (clone) {
            BattlePokemon.safeCopyOf(it)
        } else {
            BattlePokemon(it)
        }
    }.sortedBy { if (it.uuid == leadingPokemon) 0 else (indexOf(it.originalPokemon) + 1) }
}

