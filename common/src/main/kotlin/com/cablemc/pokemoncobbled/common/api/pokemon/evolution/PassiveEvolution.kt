package com.cablemc.pokemoncobbled.common.api.pokemon.evolution

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

interface PassiveEvolution : Evolution {

    fun attemptEvolution(pokemon: Pokemon): Boolean {
        if (super.test(pokemon)) {
            super.evolve(pokemon)
            return true
        }
        return false
    }

}