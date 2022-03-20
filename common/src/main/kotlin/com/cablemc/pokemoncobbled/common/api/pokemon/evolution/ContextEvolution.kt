package com.cablemc.pokemoncobbled.common.api.pokemon.evolution

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

interface ContextEvolution<T> : Evolution {

    val requiredContext: T

    fun attemptEvolution(pokemon: Pokemon, context: T): Boolean {
        if (this.testContext(pokemon, context) && super.test(pokemon)) {
            super.evolve(pokemon)
            return true
        }
        return false
    }

    fun testContext(pokemon: Pokemon, context: T): Boolean

}