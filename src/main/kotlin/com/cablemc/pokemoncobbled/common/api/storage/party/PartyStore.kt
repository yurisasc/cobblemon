package com.cablemc.pokemoncobbled.common.api.storage.party

import com.cablemc.pokemoncobbled.common.api.storage.PokemonStore
import com.cablemc.pokemoncobbled.common.api.storage.StoreCoordinates
import com.cablemc.pokemoncobbled.common.net.PokemonCobbledNetwork.sendPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.InitializePartyPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.MovePartyPokemonPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.RemovePartyPokemonPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.SetPartyPokemonPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.SwapPartyPokemonPacket
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.server.level.ServerPlayer
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
    // Allow more or fewer than 6 as a maximum perhaps?
    protected val slots = MutableList<Pokemon?>(6) { null }

    override fun iterator() = slots.filterNotNull().iterator()
    override fun getAll() = slots.filterNotNull()

    /** Gets the Pokémon at the specified slot. It will return null if the slot is empty or the given slot is out of bounds. */
    fun get(slot: Int) = slot.takeIf { it < slots.size }?.let { slots[it] }
    override fun get(position: PartyPosition) = get(position.slot)

    fun set(slot: Int, pokemon: Pokemon) = super.set(PartyPosition(slot), pokemon)
    override fun setAtPosition(position: PartyPosition, pokemon: Pokemon?) {
        if (position.slot >= slots.size) {
            throw IllegalArgumentException("Slot position is out of bounds")
        } else {
            slots[position.slot] = pokemon
            pokemon?.storeCoordinates?.set(StoreCoordinates(this, position))
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

    override fun getObservingPlayers(): Iterable<ServerPlayer> {
        TODO("Not yet implemented")
    }

    override fun sendTo(player: ServerPlayer) {
        player.sendPacket(InitializePartyPacket(false, uuid, slots.size))
        slots.forEachIndexed { index, pokemon ->
            if (pokemon != null) {
                player.sendPacket(SetPartyPokemonPacket(uuid, PartyPosition(index), pokemon))
            }
        }
    }

    override fun set(position: PartyPosition, pokemon: Pokemon) {
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

    override fun setupStoreCoordinates() {
        for (slot in slots.indices) {
            val pokemon = get(slot) ?: continue
            pokemon.storeCoordinates.set(StoreCoordinates(this, PartyPosition(slot)))
        }
    }
}

