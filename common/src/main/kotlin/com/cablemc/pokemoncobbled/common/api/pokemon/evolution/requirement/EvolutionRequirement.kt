package com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement

import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.Evolution
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.ContextEvolution
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

/**
 * Represents a requirement for an evolution.
 * Requirements are constant and will never changed regardless of the backing action.
 * For dynamic requirements see [ContextEvolution].
 *
 * See [Evolution.requirements] & [Evolution.test] for usage.
 *
 * @author Licious
 * @since March 19th, 2022
 */
fun interface EvolutionRequirement {

    /**
     * Checks if the given [Pokemon] satisfies the requirement.
     *
     * @param pokemon The [Pokemon] being queried.
     * @return If the requirement was satisfied.
     */
    fun check(pokemon: Pokemon): Boolean

}