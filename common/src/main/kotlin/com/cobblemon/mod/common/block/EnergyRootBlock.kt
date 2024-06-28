/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.CobblemonBlocks
import com.cobblemon.mod.common.CobblemonItems
import com.mojang.serialization.MapCodec
import net.minecraft.world.level.block.Block
import net.minecraft.block.ShapeContext
import net.minecraft.world.item.ItemStack
import net.minecraft.core.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.level.block.state.BlockState

@Suppress("OVERRIDE_DEPRECATION")
class EnergyRootBlock(settings: Properties) : RootBlock(settings) {

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape = AABB

    override fun shearedResultingState(): BlockState = CobblemonBlocks.BIG_ROOT.defaultState

    override fun shearedDrop(): ItemStack = CobblemonItems.ENERGY_ROOT.defaultStack

    override fun getCodec(): MapCodec<out Block> {
        return CODEC
    }

    companion object {
        private val CODEC = createCodec(::EnergyRootBlock)

        private val AABB = VoxelShapes.cuboid(0.2, 0.1, 0.2, 0.8, 1.0, 0.8)
    }

}