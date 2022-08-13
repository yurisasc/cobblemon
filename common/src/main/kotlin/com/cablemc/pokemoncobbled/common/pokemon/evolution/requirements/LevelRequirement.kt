package com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements

import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

class LevelRequirement : EvolutionRequirement {
    companion object {
        const val ADAPTER_VARIANT = "level"
    }

    val level = 1..100
    override fun check(pokemon: Pokemon) = pokemon.level in level
}