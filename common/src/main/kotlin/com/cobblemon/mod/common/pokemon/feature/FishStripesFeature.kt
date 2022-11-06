/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.feature

import com.cobblemon.mod.common.pokemon.feature.FishStripes.Companion.ALL_VALUES
import com.cobblemon.mod.common.api.pokemon.feature.EnumSpeciesFeature
import java.util.EnumSet

/**
 * Fish stripes, Basculin.
 */
enum class FishStripes {
    BLUE,
    RED;

    companion object {
        val ALL_VALUES = EnumSet.allOf(FishStripes::class.java)
    }
}

const val FISH_STRIPES = "striped"
class FishStripesFeature : EnumSpeciesFeature<FishStripes>() {
    override val name: String = FISH_STRIPES
    override fun getValues() = ALL_VALUES
}