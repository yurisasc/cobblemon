package com.cablemc.pokemoncobbled.common.pokemon.evolution

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.PassiveEvolution
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

open class LevelEvolution(
    override val id: String,
    override val to: PokemonProperties,
    val levelRange: IntRange,
    override val requirements: List<EvolutionRequirement>
) : PassiveEvolution {

    override val optional = !PokemonCobbled.config.forceLevelEvolution

    override fun attemptEvolution(pokemon: Pokemon) = super.attemptEvolution(pokemon) && this.levelRange.contains(pokemon.level)

}