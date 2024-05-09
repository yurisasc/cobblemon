/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.berry

import com.cobblemon.mod.common.api.berry.GrowthFactor
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.block.BlockState
import net.minecraft.predicate.NumberRange
import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldView
import net.minecraft.world.biome.Biome

/**
 * A [GrowthFactor] that is active based on the temperature property of a [Biome].
 *
 * @property range The range of possible [Biome.getTemperature] values.
 * @property bonusYield The range of possible bonus values.
 *
 * @author Licious
 * @since December 2nd, 2022
 */
class BiomeTemperatureGrowthFactor(
    val range: NumberRange.DoubleRange,
    val bonusYield: IntRange
) : GrowthFactor {

    override fun validateArguments() {
        if (this.bonusYield.first < 0 || this.bonusYield.last < 0) {
            throw IllegalArgumentException("$ID bonusYield must be a positive range")
        }
    }

    override fun isValid(world: WorldView, state: BlockState, pos: BlockPos): Boolean {
        val biome = world.getBiome(pos).value()
        return this.range.test(biome.temperature.toDouble())
    }

    override fun yield() = this.bonusYield.random()

    override fun minYield() = this.bonusYield.first

    override fun maxYield() = this.bonusYield.last

    companion object {

        val ID = cobblemonResource("biome_temperature")

    }

}