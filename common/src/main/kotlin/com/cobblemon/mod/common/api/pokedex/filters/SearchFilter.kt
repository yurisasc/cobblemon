package com.cobblemon.mod.common.api.pokedex.filters

import com.cobblemon.mod.common.api.pokedex.ClientPokedex
import com.cobblemon.mod.common.api.pokedex.EntryFilter
import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.pokedex.DexPokemonData
import net.minecraft.text.Text

class SearchFilter(val clientPokedex: ClientPokedex, val searchString: String) : EntryFilter(clientPokedex)  {
    override fun filter(dexPokemonData: DexPokemonData): Boolean {
        return searchString == "" || (clientPokedex.discoveryLevel(dexPokemonData.name) >= PokedexEntryProgress.ENCOUNTERED && dexPokemonData.species?.name?.contains(searchString.trim(), true) == true)
    }
}