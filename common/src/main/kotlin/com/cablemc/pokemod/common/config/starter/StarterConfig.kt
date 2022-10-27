/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.config.starter

import com.cablemc.pokemod.common.api.pokemon.PokemonProperties
import com.cablemc.pokemod.common.config.Category
import com.cablemc.pokemod.common.config.NodeCategory
import com.cablemc.pokemod.common.util.adapters.pokemonPropertiesShortAdapter
import com.google.gson.GsonBuilder
class StarterConfig {
    companion object {
        val GSON = GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .registerTypeAdapter(PokemonProperties::class.java, pokemonPropertiesShortAdapter)
            .create()
    }

    @NodeCategory(Category.Starter)
    var allowStarterOnJoin = true

    @NodeCategory(Category.Starter)
    var promptStarterOnceOnly = false

    @NodeCategory(Category.Starter)
    var starters = mutableListOf(
        StarterCategory(
            name = "Kanto",
            displayName = "pokemod.starterselection.category.kanto",
            pokemon = mutableListOf(
                PokemonProperties.parse("Bulbasaur level=5"),
                PokemonProperties.parse("Charmander level=5"),
                PokemonProperties.parse("Squirtle level=5"),
            )
        )//,
//            StarterCategory(
//                name = "Johto",
//                displayName = lang("starterselection.category.johto"),
//                pokemon = mutableListOf(
//                    PokemonProperties().also { it.level = 5 ; it.species = "Chikorita" },
//                    PokemonProperties().also { it.level = 5 ; it.species = "Cyndaquil" },
//                    PokemonProperties().also { it.level = 5 ; it.species = "Totodile" }
//                )
//            )
    )
}