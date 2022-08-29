package com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements

import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

class LevelRequirement : EvolutionRequirement {
    companion object {
        const val ADAPTER_VARIANT = "level"
    }

    val minLevel = 1
    val maxLevel = Int.MAX_VALUE
    override fun check(pokemon: Pokemon) = pokemon.level in minLevel..maxLevel
}