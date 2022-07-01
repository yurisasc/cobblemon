package com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements

import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

/**
 * An [EvolutionRequirement] for when a certain amount of [Pokemon.friendship] is expected.
 *
 * @property amount The required [Pokemon.friendship] amount, expects between 0 & 255.
 * @author Licious
 * @since March 21st, 2022
 */
class FriendshipRequirement(val amount: Int) : EvolutionRequirement {

    init {
        if (this.amount !in validRange)
            throw IllegalArgumentException("Cannot instance ${this::class.simpleName} with ${this.amount}, amount must be between ${validRange.first} & ${validRange.last}")
    }

    override fun check(pokemon: Pokemon) = pokemon.friendship >= this.amount

    companion object {

        internal const val ADAPTER_VARIANT = "friendship"
        private val validRange = 0..255

    }

}