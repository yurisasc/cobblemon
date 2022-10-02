package com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements

import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.feature.BattleCriticalHitsFeature

class BattleCriticalHitsRequirement : EvolutionRequirement {
    companion object {
        const val ADAPTER_VARIANT = BattleCriticalHitsFeature.ID
    }

    val amount = 0
    override fun check(pokemon: Pokemon): Boolean {
        val feature = pokemon.getFeature<BattleCriticalHitsFeature>(BattleCriticalHitsFeature.ID) ?: return false
        return feature.currentValue >= this.amount
    }

}