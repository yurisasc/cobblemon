/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.aspects

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.pokemon.aspect.AspectProvider
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.feature.SNAKE_PATTERN
import com.cobblemon.mod.common.pokemon.feature.SnakePatternFeature

object SnakePatternAspect : AspectProvider {
    fun provideForFeature(feature: SnakePatternFeature) = setOf("$SNAKE_PATTERN-${feature.enumValue.name.lowercase()}")

    override fun provide(pokemon: Pokemon): Set<String> {
        return provideForFeature(pokemon.getFeature(SNAKE_PATTERN) ?: return emptySet())
    }

    override fun provide(properties: PokemonProperties): Set<String> {
        return provideForFeature(properties.customProperties.filterIsInstance<SnakePatternFeature>().firstOrNull() ?: return emptySet())
    }
}