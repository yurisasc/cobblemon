/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.CobblemonItems
import net.minecraft.block.*
import net.minecraft.fluid.Fluids
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.IntProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldView
import kotlin.math.min

class MedicinalLeekBlock(settings: Settings) : PlantBlock(settings), Fertilizable {

    init {
        defaultState = stateManager.defaultState
                .with(AGE, 0)
    }

    override fun getPickStack(world: BlockView, pos: BlockPos, state: BlockState) = ItemStack(CobblemonItems.MEDICINAL_LEEK)
    private fun isMature(state: BlockState) = state.get(AGE) == MATURE_AGE
    override fun isFertilizable(world: WorldView, pos: BlockPos, state: BlockState, isClient: Boolean) = !isMature(state)
    override fun canGrow(world: World, random: Random, pos: BlockPos, state: BlockState) = true

    override fun grow(world: ServerWorld, random: Random, pos: BlockPos, state: BlockState) {
        world.setBlockState(pos, state.with(AGE, min(state.get(AGE) + 1, MATURE_AGE)), NOTIFY_LISTENERS)
    }

    @Deprecated("Deprecated in Java")
    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext) = AGE_TO_SHAPE[state.get(AGE)]
    override fun hasRandomTicks(state: BlockState) = !isMature(state)
    @Deprecated("Deprecated in Java")
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
        val fluidState = world.getFluidState(pos)
        val fluidState2 = world.getFluidState(pos.up())
        return (fluidState.fluid === Fluids.WATER) && fluidState2.fluid === Fluids.EMPTY
    }

    companion object {
        const val MATURE_AGE = 3
        val AGE: IntProperty = Properties.AGE_3
        val AGE_TO_SHAPE = arrayOf(
                createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
                createCuboidShape(0.0, 0.0, 0.0, 16.0, 5.0, 16.0),
                createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
                createCuboidShape(0.0, 0.0, 0.0, 16.0, 11.0, 16.0)
        )
    }
}