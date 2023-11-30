package com.cobblemon.mod.common.pokemon.evolution.requirements

import com.cobblemon.mod.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cobblemon.mod.common.api.pokemon.feature.IntSpeciesFeature
import com.cobblemon.mod.common.pokemon.Pokemon

class PropertyRangeRequirement : EvolutionRequirement {
    val range = IntRange(0, 256)
    val feature: String = ""

    override fun check(pokemon: Pokemon): Boolean {
        val feature: IntSpeciesFeature = pokemon.getFeature(feature) ?: return false
        return this.range.contains(feature.value)
    }

    companion object {
        const val ADAPTER_VARIANT = "property_range"
    }
}