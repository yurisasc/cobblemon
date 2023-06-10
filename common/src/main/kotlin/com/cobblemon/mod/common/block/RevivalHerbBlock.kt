/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import kotlin.math.min
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.Fertilizable
import net.minecraft.block.PlantBlock
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.IntProperty
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldView

class RevivalHerbBlock(settings: Settings) : PlantBlock(settings), Fertilizable {
    companion object {
        const val MIN_AGE = 1
        const val MAX_AGE = 9
        val AGE = IntProperty.of("age", MIN_AGE, MAX_AGE)
        val IS_WILD = BooleanProperty.of("is_wild")
    }

    init {
        defaultState = this.stateManager.defaultState.with(AGE, MIN_AGE).with(IS_WILD, false)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(AGE)
        builder.add(IS_WILD)
    }

    override fun canGrow(world: World, random: Random, pos: BlockPos, state: BlockState): Boolean {
        return state.get(AGE) < MAX_AGE
    }

    override fun grow(world: ServerWorld, random: Random, pos: BlockPos, state: BlockState) {
        world.setBlockState(pos, state.with(AGE, min(state.get(AGE) + 1, MAX_AGE)), NOTIFY_LISTENERS)
    }

    override fun isFertilizable(world: WorldView, pos: BlockPos, state: BlockState, isClient: Boolean): Boolean {
        return state.get(AGE) < MAX_AGE
    }

    override fun canPlantOnTop(floor: BlockState, world: BlockView, pos: BlockPos): Boolean {
        val topState = world.getBlockState(pos.up())
        return if (topState.block is RevivalHerbBlock && topState.get(IS_WILD)) {
            true
        } else floor.isOf(Blocks.FARMLAND)
    }

    override fun hasRandomTicks(state: BlockState) = state.get(AGE) < MAX_AGE
}