/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block.grower

import com.cobblemon.mod.common.world.feature.apricorn.CobblemonApricornConfiguredFeatures
import com.cobblemon.mod.common.api.apricorn.Apricorn
import net.minecraft.block.sapling.SaplingGenerator
import net.minecraft.util.math.random.Random
class ApricornTreeGrower(private val apricorn: Apricorn) : SaplingGenerator() {
    override fun getTreeFeature(random: Random, bl: Boolean) = when (this.apricorn) {
        Apricorn.BLACK -> CobblemonApricornConfiguredFeatures.BLACK_APRICORN_TREE_KEY
        Apricorn.BLUE -> CobblemonApricornConfiguredFeatures.BLUE_APRICORN_TREE_KEY
        Apricorn.GREEN -> CobblemonApricornConfiguredFeatures.GREEN_APRICORN_TREE_KEY
        Apricorn.PINK -> CobblemonApricornConfiguredFeatures.PINK_APRICORN_TREE_KEY
        Apricorn.RED -> CobblemonApricornConfiguredFeatures.RED_APRICORN_TREE_KEY
        Apricorn.WHITE -> CobblemonApricornConfiguredFeatures.WHITE_APRICORN_TREE_KEY
        Apricorn.YELLOW -> CobblemonApricornConfiguredFeatures.YELLOW_APRICORN_TREE_KEY
    }
}