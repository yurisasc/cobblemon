package com.cablemc.pokemoncobbled.client.storage

import com.cablemc.pokemoncobbled.common.api.storage.party.PartyPosition
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.mod.PokemonCobbledMod.LOGGER
import net.minecraftforge.client.event.ClientPlayerNetworkEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import java.util.UUID

/**
 * Manages all known Pokémon stores on the client. This holds onto the player's party and PC permanently, but can also
 * hold other stores arbitrarily.
 *
 * @author Hiroku
 * @since November 28th, 2021
 */
class ClientStorageManager {
    var myParty = ClientParty(UUID.randomUUID(), 1)
    val partyStores = mutableMapOf<UUID, ClientParty>()

    var myPC = ClientPC(UUID.randomUUID(), 1)
    val pcStores = mutableMapOf<UUID, ClientPC>()

    fun locatePokemon(storeID: UUID, pokemonID: UUID): Pokemon? {
        partyStores[storeID]?.let { return it.findByUUID(pokemonID) } ?: return pcStores[storeID]?.findByUUID(pokemonID)
    }

    fun createParty(mine: Boolean, uuid: UUID, slots: Int) {
        val party = ClientParty(uuid, slots)
        partyStores[uuid] = party
        if (mine) {
            myParty = party
        }
    }

    fun setPartyPokemon(storeID: UUID, position: PartyPosition, pokemon: Pokemon) {
        val party = partyStores[storeID]
            ?: return LOGGER.error("Tried setting a Pokémon in position $position for party store $storeID but no such store found.")
        party.set(position, pokemon)
    }

    fun setPartyStore(storeID: UUID) {
        myParty = partyStores[storeID] ?: throw IllegalArgumentException("Was told to set party store to $storeID but no such store is known!")
    }

    fun removeFromParty(storeID: UUID, pokemonID: UUID) {
        partyStores[storeID]?.remove(pokemonID)
    }

    fun moveInParty(storeID: UUID, pokemonID: UUID, newPosition: PartyPosition) {
        partyStores[storeID]?.move(pokemonID, newPosition)
    }

    fun swapInParty(storeID: UUID, pokemonID1: UUID, pokemonID2: UUID) {
        partyStores[storeID]?.swap(pokemonID1, pokemonID2)
    }

    @SubscribeEvent
    fun on(event: ClientPlayerNetworkEvent.LoggedOutEvent) {
        partyStores.clear()
        pcStores.clear()
        myPC = ClientPC(UUID.randomUUID(), 1)
        myParty = ClientParty(UUID.randomUUID(), 1)
    }
}