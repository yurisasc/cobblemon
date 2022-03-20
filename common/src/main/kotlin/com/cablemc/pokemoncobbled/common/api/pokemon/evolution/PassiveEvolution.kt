package com.cablemc.pokemoncobbled.common.api.pokemon.evolution

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.evolution.LevelEvolution

/**
 * Represents an evolution of a [Pokemon] that can occur without any additional context or actions.
 * For the default implementation see [LevelEvolution].
 *
 * @author Licious
 * @since March 19th, 2022
 */
interface PassiveEvolution : Evolution {

    /**
     * Checks if the given [Pokemon] satisfies the requirements.
     * If yes the evolution will attempt to start.
     *
     * @param pokemon The [Pokemon] being tested.
     * @return If the [Pokemon] will evolve.
     */
    fun attemptEvolution(pokemon: Pokemon): Boolean {
        if (super.test(pokemon)) {
            super.evolve(pokemon)
            return true
        }
        return false
    }

}