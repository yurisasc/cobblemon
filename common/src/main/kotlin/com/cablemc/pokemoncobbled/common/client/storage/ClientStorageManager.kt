package com.cablemc.pokemoncobbled.common.client.storage

import com.cablemc.pokemoncobbled.common.PokemonCobbled.LOGGER
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.EvolutionController
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.EvolutionDisplay
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyPosition
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.evolution.controller.CobbledClientEvolutionController
import java.util.*

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

    var selectedSlot = -1
    private var selectedPokemon: UUID? = null
    private val pendingEvolutions = hashMapOf<UUID, EvolutionController<EvolutionDisplay>>()

    fun shiftSelected(forward: Boolean) {
        val partyHasSome = myParty.slots.any { it != null }
        if (!partyHasSome) {
            selectedSlot = 0
            selectedPokemon = null
            return
        }

        selectedSlot += if (forward) 1 else -1
        if (selectedSlot >= myParty.slots.size) {
            selectedSlot = -1
            shiftSelected(forward)
        } else if (selectedSlot < 0) {
            selectedSlot = myParty.slots.size
            shiftSelected(forward)
        } else if (myParty.get(selectedSlot) == null) {
            shiftSelected(forward)
        } else {
            selectedPokemon = myParty.get(selectedSlot)?.uuid
        }
    }

    fun checkSelectedPokemon() {
        if (selectedSlot == -1) {
            val pokemon = myParty.firstOrNull { it != null } ?: return
            selectedSlot = myParty.slots.indexOf(pokemon)
            selectedPokemon = pokemon.uuid
        } else if (selectedPokemon == null) {
            selectedPokemon = myParty.get(PartyPosition(selectedSlot))?.uuid ?: run {
                selectedSlot = -1
                checkSelectedPokemon()
                null
            }
        } else if (myParty.getPosition(selectedPokemon!!) != selectedSlot) {
            val foundSlot = myParty.getPosition(selectedPokemon!!)
            if (foundSlot != -1) {
                selectedSlot = foundSlot
            } else {
                selectedPokemon = null
                checkSelectedPokemon()
            }
        } else if (selectedSlot >= myParty.slots.size) {
            selectedSlot = -1
            checkSelectedPokemon()
        }
    }

    fun locatePokemon(storeID: UUID, pokemonID: UUID): Pokemon? {
        partyStores[storeID]?.let { return it.findByUUID(pokemonID) } ?: return pcStores[storeID]?.findByUUID(pokemonID)
    }

    fun createParty(mine: Boolean, uuid: UUID, slots: Int) {
        val party = ClientParty(uuid, slots)
        partyStores[uuid] = party
        if (mine) {
            myParty = party
            checkSelectedPokemon()
        }
    }

    fun setPartyPokemon(storeID: UUID, position: PartyPosition, pokemon: Pokemon) {
        val party = partyStores[storeID]
            ?: return LOGGER.error("Tried setting a Pokémon in position $position for party store $storeID but no such store found.")
        party.set(position, pokemon)
        checkSelectedPokemon()
    }

    fun setPartyStore(storeID: UUID) {
        myParty = partyStores[storeID] ?: throw IllegalArgumentException("Was told to set party store to $storeID but no such store is known!")
        checkSelectedPokemon()
    }

    fun removeFromParty(storeID: UUID, pokemonID: UUID) {
        partyStores[storeID]?.remove(pokemonID)
        checkSelectedPokemon()
    }

    fun moveInParty(storeID: UUID, pokemonID: UUID, newPosition: PartyPosition) {
        partyStores[storeID]?.move(pokemonID, newPosition)
        checkSelectedPokemon()
    }

    fun swapInParty(storeID: UUID, pokemonID1: UUID, pokemonID2: UUID) {
        partyStores[storeID]?.swap(pokemonID1, pokemonID2)
        checkSelectedPokemon()
    }

    /**
     * Adds a pending evolution for the given Pokémon.
     * This should only be called from S2C packets in order to sync correctly.
     *
     * @param pokemon The [Pokemon] getting an evolution proposed.
     * @param evolution The [EvolutionDisplay] of the proposition.
     */
    fun addPendingEvolution(pokemon: Pokemon, evolution: EvolutionDisplay) {
        this.pendingEvolutions.getOrPut(pokemon.uuid) { CobbledClientEvolutionController(pokemon) }.add(evolution)
    }

    /**
     * Clears all pending evolutions for the given Pokémon.
     * This should only be called from S2C packets in order to sync correctly.
     *
     * @param pokemon The [Pokemon] getting the proposed evolutions cleared.
     */
    fun clearPendingEvolutions(pokemon: Pokemon) {
        this.pendingEvolutions.remove(pokemon.uuid)
    }

    /**
     * Removes a specific pending evolution for the given Pokémon.
     * This should only be called from S2C packets in order to sync correctly.
     *
     * @param pokemon The [Pokemon] getting an evolution proposed.
     * @param evolution The [EvolutionDisplay] of the proposition.
     */
    fun removePendingEvolution(pokemon: Pokemon, evolution: EvolutionDisplay) {
        this.pendingEvolutions[pokemon.uuid]?.remove(evolution)
    }

    /**
     * Checks if the given Pokémon has pending evolutions.
     *
     * @param pokemon The [Pokemon] being checked for proposed evolutions.
     * @return If there are any pending evolutions.
     */
    fun hasPendingEvolutions(pokemon: Pokemon): Boolean = this.pendingEvolutionsOf(pokemon)?.isNotEmpty() ?: false

    /**
     * Gets the evolution controller of the given Pokémon if existing.
     *
     * @param pokemon The [Pokemon] getting an evolution proposed.
     * @return The pending evolutions if any exist.
     */
    fun pendingEvolutionsOf(pokemon: Pokemon): EvolutionController<EvolutionDisplay>? = this.pendingEvolutions[pokemon.uuid]

    fun onLogin() {
        partyStores.clear()
        pcStores.clear()
        this.pendingEvolutions.clear()
        myPC = ClientPC(UUID.randomUUID(), 1)
        myParty = ClientParty(UUID.randomUUID(), 1)
        checkSelectedPokemon()
    }

}