package com.cablemc.pokemoncobbled.common.config.starter

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.config.Category
import com.cablemc.pokemoncobbled.common.config.NodeCategory
import com.cablemc.pokemoncobbled.common.util.adapters.pokemonPropertiesShortAdapter
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
            displayName = "pokemoncobbled.starterselection.category.kanto",
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