package com.cobblemon.mod.common.pokemon.evolution.requirements

import com.cobblemon.mod.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.pokemon.Pokemon

class StatCompareRequirement : EvolutionRequirement {
    companion object {
        const val ADAPTER_VARIANT = "stat_compare"
    }

    val highStat = Stats.ATTACK.name
    val lowStat = Stats.DEFENCE.name

    override fun check(pokemon: Pokemon): Boolean {
        return pokemon.getStat(Stats.getStat(highStat)) > pokemon.getStat(Stats.getStat(lowStat))
    }
}