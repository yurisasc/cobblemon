package com.cobblemon.mod.common.config.pokedex

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.config.Category
import com.cobblemon.mod.common.config.NodeCategory
import com.cobblemon.mod.common.util.adapters.IdentifierAdapter
import com.cobblemon.mod.common.util.adapters.pokemonPropertiesShortAdapter
import com.google.gson.GsonBuilder
import net.minecraft.util.Identifier

class PokedexDeltaConfig {
    companion object {
        val GSON = GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .registerTypeAdapter(Identifier::class.java, IdentifierAdapter)
            .create()
    }

    @NodeCategory(Category.Pokedex)
    var removeDex = mutableListOf<Identifier>()

    @NodeCategory(Category.Pokedex)
    var addDex = mutableListOf<PokedexCategory>()

    @NodeCategory(Category.Pokedex)
    var removePokemon = mutableListOf<Identifier>()

    @NodeCategory(Category.Pokedex)
    var addPokemon = mutableListOf<PokedexEntryCategory>()
}