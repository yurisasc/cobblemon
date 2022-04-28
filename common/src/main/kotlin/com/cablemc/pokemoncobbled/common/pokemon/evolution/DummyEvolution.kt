package com.cablemc.pokemoncobbled.common.pokemon.evolution

import com.cablemc.pokemoncobbled.common.api.moves.MoveTemplate
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.Evolution
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

internal class DummyEvolution : Evolution {

    override val id = "dummy"
    override val result: PokemonProperties = PokemonProperties()
    override var optional = false
    override var consumeHeldItem = false
    override val requirements: MutableSet<EvolutionRequirement> = mutableSetOf()
    override val learnableMoves: MutableSet<MoveTemplate> = mutableSetOf()

    override fun test(pokemon: Pokemon) = false

    override fun evolve(pokemon: Pokemon) {}

}