/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.storage.player

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.storage.player.client.ClientInstancedPlayerData
import com.cobblemon.mod.common.api.storage.player.client.ClientPokedexPlayerData
import com.cobblemon.mod.common.api.storage.pokedex.PokedexEntry
import com.cobblemon.mod.common.pokedex.DexStats
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.util.Identifier
import org.apache.http.util.Args
import java.util.UUID

/**
 * [InstancedPlayerData] that tracks pokedex progress
 */
class PokedexPlayerData(
    override val uuid: UUID,
    val pokedexEntries: HashMap<Identifier, PokedexEntry>
) : InstancedPlayerData {
    override fun toClientData(): ClientInstancedPlayerData {
        return ClientPokedexPlayerData(pokedexEntries)
    }

    fun wildPokemonEncountered(pokemon: Pokemon) {
        savePokemonEvent(pokemon, PokedexEntry::wildPokemonEncountered)
    }

    fun pokemonCaptured(pokemon: Pokemon){
        savePokemonEvent(pokemon, PokedexEntry::pokemonCaught)
    }

    fun pokemonEncounteredBattle(pokemon: Pokemon) {
        savePokemonEvent(pokemon, PokedexEntry::pokemonEncounteredBattle)
    }

    //Kinda weird method
    fun getInstancedPiece(pokemon: Pokemon): PokedexPlayerData {
        val id = pokemon.species.resourceIdentifier
        val formMap = this.pokedexEntries[id]?.progressMap ?: mutableMapOf()
        val speciesMap = hashMapOf(
            Pair(id, PokedexEntry(id, formMap))
        )
        return PokedexPlayerData(uuid, speciesMap)
    }

    //Returns a new [PokedexPlayerData] for incremental sending
    fun savePokemonEvent(pokemon: Pokemon, dexEntryFunction: (PokedexEntry, String) -> (Unit)) {
        val speciesId = pokemon.species.resourceIdentifier
        if (!pokedexEntries.containsKey(speciesId)) {
            pokedexEntries[speciesId] = PokedexEntry(
                    pokemon.species.resourceIdentifier,
                    mutableMapOf()
            )
        }
        val dexEntry = pokedexEntries[speciesId]!!
        val formString = PokedexEntry.formToFormString(pokemon.species.getForm(emptySet()), pokemon.shiny)
        dexEntryFunction(dexEntry, formString)
        Cobblemon.playerDataManager.saveSingle(this, PlayerInstancedDataStoreType.POKEDEX)
    }
}