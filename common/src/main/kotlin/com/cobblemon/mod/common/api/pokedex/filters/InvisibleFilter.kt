package com.cobblemon.mod.common.api.pokedex.filters

import com.cobblemon.mod.common.api.pokedex.ClientPokedex
import com.cobblemon.mod.common.api.pokedex.EntryFilter
import com.cobblemon.mod.common.pokedex.DexPokemonData

class InvisibleFilter(val clientPokedex: ClientPokedex) : EntryFilter(clientPokedex) {
    override fun filter(dexPokemonData: DexPokemonData): Boolean {
        return clientPokedex.discovered(dexPokemonData.name) || !dexPokemonData.invisibleUntilFound
    }
}