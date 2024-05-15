package com.cobblemon.mod.common.api.pokedex

import com.cobblemon.mod.common.pokedex.DexPokemonData

abstract class EntryFilter(clientPokedex: ClientPokedex) {
    abstract fun filter(dexPokemonData: DexPokemonData): Boolean
}