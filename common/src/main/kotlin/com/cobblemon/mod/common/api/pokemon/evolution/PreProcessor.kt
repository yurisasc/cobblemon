package com.cobblemon.mod.common.api.pokemon.evolution

import com.cobblemon.mod.common.pokemon.Pokemon

/**
 * TODO
 */
fun interface PreProcessor {
    fun create(pokemon: Pokemon): EvolutionController<*, *>
}