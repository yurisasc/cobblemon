package com.cablemc.pokemoncobbled.common.api.pokemon.evolution

import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.ContextEvolutionRequirement
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionContext
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

interface ContextEvolution<T: EvolutionContext> : Evolution {

    val contextRequirements: List<ContextEvolutionRequirement<T>>

    fun attemptEvolution(pokemon: Pokemon, context: T): Boolean {
        if (this.contextRequirements.all { requirement -> requirement.check(pokemon, context) } && super.check(pokemon)) {
            super.evolve(pokemon)
            return true
        }
        return false
    }

}