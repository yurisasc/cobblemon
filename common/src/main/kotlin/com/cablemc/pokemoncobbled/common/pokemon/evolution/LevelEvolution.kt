package com.cablemc.pokemoncobbled.common.pokemon.evolution

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.PassiveEvolution
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemoncobbled.common.config.constraint.IntConstraint
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

open class LevelEvolution(
    override val id: String,
    override val result: PokemonProperties,
    @IntConstraint(1, 100)
    val levels: IntRange,
    override val optional: Boolean,
    override val consumeHeldItem: Boolean,
    override val requirements: List<EvolutionRequirement>
) : PassiveEvolution {

    override fun attemptEvolution(pokemon: Pokemon) = (pokemon.level in this.levels || pokemon.level > this.levels.last) && super.attemptEvolution(pokemon)

    companion object {

        internal const val ADAPTER_VARIANT = "level_up"

    }

}