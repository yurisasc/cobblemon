package com.cablemc.pokemoncobbled.common.pokemon.evolution

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.ContextEvolution
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.ContextEvolutionRequirement
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionContext
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemoncobbled.common.pokemon.Species

open class TradeEvolution(
    override val id: String,
    override val to: PokemonProperties,
    val species: Species?,
    override val requirements: List<EvolutionRequirement>,
) : ContextEvolution<TradeEvolution.Context> {

    override val optional = !PokemonCobbled.config.forceTradeEvolution

    override val contextRequirements: List<ContextEvolutionRequirement<Context>> = listOf(ContextEvolutionRequirement { _, context -> this.species?.nationalPokedexNumber == context.species?.nationalPokedexNumber })

    open class Context(val species: Species?) : EvolutionContext

}