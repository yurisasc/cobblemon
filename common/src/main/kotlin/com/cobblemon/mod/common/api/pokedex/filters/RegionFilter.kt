package com.cobblemon.mod.common.api.pokedex.filters

import com.cobblemon.mod.common.api.pokedex.ClientPokedex
import com.cobblemon.mod.common.api.pokedex.EntryFilter
import com.cobblemon.mod.common.api.pokedex.PokedexJSONRegistry
import com.cobblemon.mod.common.pokedex.DexData
import com.cobblemon.mod.common.pokedex.DexPokemonData

class RegionFilter(clientPokedex: ClientPokedex, val region: DexData) : EntryFilter(clientPokedex) {
    override fun filter(dexPokemonData: DexPokemonData): Boolean {
        if (region.pokemonList.contains(dexPokemonData)) return true
        val containedDexes = region.containedDexes.map { PokedexJSONRegistry.getByIdentifier(it)!! }
        containedDexes.forEach { if (it.pokemonList.contains(dexPokemonData)) return true }
        return false
    }
}