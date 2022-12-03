/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.events.berry

import com.cobblemon.mod.common.api.berry.Berry
import com.cobblemon.mod.common.api.berry.GrowthFactor
import net.minecraft.block.BlockState
import net.minecraft.entity.LivingEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * The event fired when [Berry.calculateYield] is invoked.
 *
 * @property placer The [LivingEntity] triggering the calculation.
 * @property yield The current yield of berries.
 * @property passedGrowthFactors The [Berry.growthFactors] where [GrowthFactor.isValid] was true.
 */
class BerryYieldCalculationEvent(
    override val berry: Berry,
    val world: World,
    val state: BlockState,
    val pos: BlockPos,
    val placer: LivingEntity?,
    yield: Int,
    val passedGrowthFactors: Collection<GrowthFactor>
) : BerryEvent {

    /**
     * The amount of berries this tree will yield.
     * This value must cannot exceed [Berry.maxYield] or be lesser than 0.
     */
    var yield: Int = yield
        private set

    /**
     * Sets a new value for [yield].
     *
     * @param newYield The new value for the yield.
     *
     * @throws IllegalArgumentException If [newYield] exceeds [Berry.maxYield].
     */
    fun setYield(newYield: Int) {
        val max = this.berry.maxYield()
        if (newYield > max) {
            throw IllegalArgumentException("Cannot set the berry yield for ${this.berry.identifier} above $max")
        }
        if (newYield < 0) {
            throw IllegalArgumentException("A berry tree cannot yield a negative amount of berries")
        }
        this.yield = newYield
    }

}