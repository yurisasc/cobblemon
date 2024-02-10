/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.api.tags.CobblemonBlockTags
import net.minecraft.block.*
import net.minecraft.item.ItemPlacementContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.WorldAccess
import net.minecraft.world.WorldView

@Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
class TumblestoneBlock(
    settings: Settings,
    stage: Int,
    height: Int,
    xzOffset: Int,
    nextStage: Block?
) : GrowableStoneBlock(settings, stage, height, xzOffset, nextStage) {
    override fun canGrow(pos: BlockPos, world: BlockView): Boolean {
        if (stage == MAX_STAGE) return false
        val iterator: Iterator<BlockPos> =
            BlockPos.iterate(pos.add(-1, -1, -1), pos.add(1, 1, 1))
                .iterator()

        var blockPos: BlockPos
        do { if (!iterator.hasNext()) { return false }
            blockPos = iterator.next()
        } while (!world.getBlockState(blockPos).isIn(CobblemonBlockTags.TUMBLESTONE_HEAT_SOURCE))

        return true
    }

}