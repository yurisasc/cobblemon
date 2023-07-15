/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.block.VivichokeBlock
import kotlin.math.min
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.Fertilizable
import net.minecraft.block.PlantBlock
import net.minecraft.block.ShapeContext
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.IntProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldView

class VivichokeBlock(settings: Settings) : PlantBlock(settings), Fertilizable {
    init {
        defaultState = stateManager.defaultState
            .with(AGE, 0)
    }

    override fun getPickStack(world: BlockView, pos: BlockPos, state: BlockState) = ItemStack(CobblemonItems.VIVICHOKE)
    private fun isMature(state: BlockState) = state.get(AGE) == MATURE_AGE
    override fun isFertilizable(world: WorldView, pos: BlockPos, state: BlockState, isClient: Boolean) = !isMature(state)
    override fun canGrow(world: World, random: Random, pos: BlockPos, state: BlockState) = true

    override fun grow(world: ServerWorld, random: Random, pos: BlockPos, state: BlockState) {
        world.setBlockState(pos, state.with(AGE, min(state.get(AGE) + 1, MATURE_AGE)), NOTIFY_LISTENERS)
    }

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext) = AGE_TO_SHAPE[state.get(AGE)]
    override fun hasRandomTicks(state: BlockState) = !isMature(state)
    override fun randomTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        if (world.getBaseLightLevel(pos, 0) >= 0) {
            if (isMature(state)) return
            if (random.nextInt(50) != 0) return

            val currentAge = state.get(AGE)
            world.setBlockState(pos, defaultState.with(AGE, currentAge + 1), NOTIFY_LISTENERS)
        }
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(AGE)
    }

    override fun canPlantOnTop(floor: BlockState, world: BlockView, pos: BlockPos): Boolean {
        return floor.isOf(Blocks.FARMLAND)
    }

    companion object {
        const val MATURE_AGE = 7
        val AGE: IntProperty = Properties.AGE_7
        val STAGE_0_SHAPE = createCuboidShape(5.0, -1.0, 5.0, 11.0, 1.0, 11.0)
        val STAGE_1_SHAPE = createCuboidShape(3.0, -1.0, 3.0, 13.0, 2.0, 13.0)
        val STAGE_2_SHAPE = createCuboidShape(3.0, -1.0, 3.0, 13.0, 4.0, 13.0)
        val STAGE_3_SHAPE = createCuboidShape(3.0, -1.0, 3.0, 13.0, 8.0, 13.0)
        val AGE_TO_SHAPE = arrayOf(
            STAGE_0_SHAPE,
            STAGE_1_SHAPE,
            STAGE_1_SHAPE,
            STAGE_1_SHAPE,
            STAGE_2_SHAPE,
            STAGE_2_SHAPE,
            STAGE_2_SHAPE,
            STAGE_3_SHAPE,
        )
    }
}