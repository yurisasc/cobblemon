/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.world.feature

import com.cobblemon.mod.common.block.MedicinalLeekBlock
import net.minecraft.block.Block
import net.minecraft.fluid.Fluids
import net.minecraft.util.math.BlockPos
import net.minecraft.world.StructureWorldAccess
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.SingleStateFeatureConfig
import net.minecraft.world.gen.feature.util.FeatureContext

class MedicinalLeekFeature : Feature<SingleStateFeatureConfig>(SingleStateFeatureConfig.CODEC) {

    override fun generate(context: FeatureContext<SingleStateFeatureConfig>): Boolean {

        println("Medicinal Leek Attempted Generation")
        val world = context.world
        val blockPos = context.origin
        val blockState = context.config.state
        val floor = blockPos.down()

        if (world.getBlockState(floor) != Fluids.WATER) return false

        val validPlacements = getValidPositions(world, blockPos)
        if (validPlacements.isEmpty()) return false

        val minAge = MedicinalLeekBlock.MATURE_AGE - 1
        val maxAge = MedicinalLeekBlock.MATURE_AGE

        world.setBlockState(blockPos, blockState.with(MedicinalLeekBlock.AGE, context.random.nextBetween(minAge, maxAge)), Block.NOTIFY_LISTENERS)
        validPlacements.shuffled().take(2).forEach { position ->
            world.setBlockState(position, blockState.with(MedicinalLeekBlock.AGE, context.random.nextBetween(minAge, maxAge)), Block.NOTIFY_LISTENERS)
        }
        println("Medicinal Leek Generated")
        return true
    }

    private fun getValidPositions(world: StructureWorldAccess, origin: BlockPos): List<BlockPos> {
        val validPositions = mutableListOf<BlockPos>()

        for (x in -1..1) {
            for (y in -1..1) {
                for (z in -1..1) {
                    if (x == 0 && z == 0) continue
                    val offsetPos = origin.add(x, y, z)
                    val floorBlockState = world.getBlockState(offsetPos.down())
                    if (world.isAir(offsetPos) && floorBlockState == Fluids.WATER) {
                        validPositions.add(offsetPos)
                    }
                }
            }
        }

        return validPositions
    }

}