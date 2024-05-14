package com.cobblemon.mod.common.pokemon.evolution.requirements

import com.cobblemon.mod.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.pokemon.Pokemon

class StatEqualRequirement : EvolutionRequirement {
    companion object {
        const val ADAPTER_VARIANT = "stat_equal"
    }

    val statOne = Stats.ATTACK.name
    val statTwo = Stats.DEFENCE.name

    override fun check(pokemon: Pokemon): Boolean {
        return pokemon.getStat(Stats.getStat(statOne)) == pokemon.getStat(Stats.getStat(statTwo))
    }
}