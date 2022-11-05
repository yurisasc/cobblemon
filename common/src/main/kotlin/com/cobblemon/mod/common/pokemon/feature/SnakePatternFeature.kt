/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.feature

import com.cobblemon.mod.common.api.pokemon.feature.EnumSpeciesFeature
import java.util.EnumSet

/**
 * A pattern on a snake Pokémon, typically Arbok.
 */
enum class SnakePattern {
    CLASSIC,
    LEGACY,
    ATTACK,
    DARK,
    ELUSIVE,
    HEART,
    SPEED,
    SOUND;

    companion object {
        val ALL_VALUES = EnumSet.allOf(SnakePattern::class.java)
    }
}

const val SNAKE_PATTERN = "snake-pattern"
class SnakePatternFeature : EnumSpeciesFeature<SnakePattern>() {
    override val name: String = SNAKE_PATTERN
    override fun getValues() = SnakePattern.ALL_VALUES
}