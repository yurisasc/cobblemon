package com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements

import com.cablemc.pokemoncobbled.common.api.moves.MoveTemplate
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

/**
 * An [EvolutionRequirement] for when a certain [MoveTemplate] is expected in the [Pokemon.moveSet].
 *
 * @property move The required [MoveTemplate].
 * @author Licious
 * @since March 21st, 2022
 */
class MoveSetRequirement(val move: MoveTemplate) : EvolutionRequirement {

    override fun check(pokemon: Pokemon) = pokemon.moveSet.getMoves().any { move -> move.name.equals(this.move.name, true) }

    companion object {

        internal const val ADAPTER_VARIANT = "has_move"

    }

}