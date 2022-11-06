package com.cobblemon.mod.common.pokemon.aspects

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.pokemon.aspect.AspectProvider
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.feature.FISH_STRIPES
import com.cobblemon.mod.common.pokemon.feature.FishStripesFeature

object FishStripesAspect : AspectProvider {
    fun provideForFeature(feature: FishStripesFeature) = setOf("${feature.enumValue.name.lowercase()}striped")

    override fun provide(pokemon: Pokemon): Set<String> {
        return provideForFeature(pokemon.getFeature(FISH_STRIPES) ?: return emptySet())
    }

    override fun provide(properties: PokemonProperties): Set<String> {
        return provideForFeature(properties.customProperties.filterIsInstance<FishStripesFeature>().firstOrNull() ?: return emptySet())
    }
}