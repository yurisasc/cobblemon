package com.cablemc.pokemoncobbled.common.api.pokemon.evolution

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.evolution.ItemInteractionEvolution
import com.cablemc.pokemoncobbled.common.pokemon.evolution.TradeEvolution

/**
 * Represents an evolution of a [Pokemon] that can only occur during specific actions and with added context.
 * For the default implementation see [ItemInteractionEvolution] & [TradeEvolution].
 *
 * @author Licious
 * @since March 19th, 2022
 */
interface ContextEvolution<T> : Evolution {

    /**
     * The target context for this [Evolution] to even be tested.
     */
    val requiredContext: T

    /**
     * Attempts to evolve the given [Pokemon] under the given context of type [T].
     *
     * @param pokemon The [Pokemon] attempting to evolve.
     * @param context The context of this query.
     * @return If the evolution was successful.
     */
    fun attemptEvolution(pokemon: Pokemon, context: T): Boolean {
        if (this.testContext(pokemon, context) && super.test(pokemon)) {
            super.evolve(pokemon)
            return true
        }
        return false
    }

    /**
     * Checks if the given context is valid for the [requiredContext].
     *
     * @param pokemon The [Pokemon] attempting to evolve.
     * @param context The context of this query.
     * @return If the context matched the [requiredContext].
     */
    fun testContext(pokemon: Pokemon, context: T): Boolean

}