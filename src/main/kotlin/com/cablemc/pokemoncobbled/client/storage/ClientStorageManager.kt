package com.cablemc.pokemoncobbled.client.storage

import com.cablemc.pokemoncobbled.common.api.storage.party.PartyPosition
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.mod.PokemonCobbledMod.LOGGER
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
}