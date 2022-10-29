package com.cablemc.pokemod.common.api.pokemon.friendship

import com.cablemc.pokemod.common.pokemon.Pokemon

/**
 * A calculator for the amount of friendship to mutate based on different triggers.
 * You can find some default implementations in the [FriendshipMutationCalculator.Companion].
 *
 * @author Licious
 * @since October 29th, 2022
 */
fun interface FriendshipMutationCalculator {

    /**
     * Resolve the amount of friendship to mutate.
     *
     * @param pokemon The [Pokemon] being mutated.
     * @return The amount of friendship to mutate.
     */
    fun calculate(pokemon: Pokemon): Int

    companion object {

        /**
         * The calculator used for level up friendship yields in generation 8.
         * For more information see this [Bulbapedia](https://bulbapedia.bulbagarden.net/wiki/Friendship#Generation_VIII) entry.
         * It will yield 3 between 0 and 99 friendship, 2 between 100 and 199 otherwise 0.
         */
        val GENERATION_8_LEVEL_UP = FriendshipMutationCalculator { pokemon ->
            when {
                pokemon.friendship <= 99 -> 3
                pokemon.friendship <= 199 -> 2
                else -> 0
            }

        }

    }

}