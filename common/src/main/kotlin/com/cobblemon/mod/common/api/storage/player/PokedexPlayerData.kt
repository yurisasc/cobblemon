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
import com.cobblemon.mod.common.pokedex.DexStats
import com.cobblemon.mod.common.pokedex.PokedexEntry
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.util.Identifier
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
        val speciesId = pokemon.species.resourceIdentifier
        if (!pokedexEntries.containsKey(speciesId)) {
            pokedexEntries[speciesId] = PokedexEntry(
                pokemon.species.resourceIdentifier,
                mutableMapOf()
            )
        }
        val dexEntry = pokedexEntries[speciesId]!!
        val formString = PokedexEntry.formToFormString(pokemon.species.getForm(emptySet()), pokemon.shiny)
        dexEntry.pokemonEncountered(formString, true)
        Cobblemon.playerDataManager.saveSingle(this, PlayerInstancedDataStoreType.POKEDEX)
    }

}