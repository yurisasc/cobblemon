/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.world.block

import com.cablemc.pokemod.common.PokemodBlockTags
import com.cablemc.pokemod.common.item.ApricornItem
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.CocoaBlock
import net.minecraft.block.ShapeContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.state.property.IntProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldView
import net.minecraft.world.event.GameEvent
import java.util.function.Supplier

class ApricornBlock(settings: Settings, private val itemSupplier: Supplier<ApricornItem>) : CocoaBlock(settings) {

    // Do not remove this, we need to overwrite the cocoa beans properties
    init {
        this.defaultState = this.stateManager.defaultState
            .with(FACING, Direction.NORTH)
            .with(AGE, MIN_AGE)
    }

    @Deprecated("DEPRECATION")
    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        val blockState = world.getBlockState(pos.offset(state.get(FACING) as Direction))
        return blockState.isIn(PokemodBlockTags.APRICORN_LEAVES)
    }

    @Deprecated("Deprecated in Java")
    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        if (state.get(AGE) == MAX_AGE) {
            dropStack(world, pos, ItemStack(this.itemSupplier.get()))
            // Don't use default as we want to keep the facing
            val resetState = state.with(AGE, MIN_AGE)
            world.setBlockState(pos, resetState, 2)
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, resetState))
            return ActionResult.success(world.isClient)
        }
        return super.onUse(state, world, pos, player, hand, hit)
    }

    // We need to point back to the actual apricorn item, see SweetBerryBushBlock for example
    override fun getPickStack(world: BlockView, pos: BlockPos, state: BlockState) = ItemStack(this.itemSupplier.get())

    @Deprecated("Deprecated in Java")
    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        val age = state.get(AGE)
        return when (state.get(FACING)) {
            Direction.NORTH -> NORTH_AABB[age]
            Direction.EAST -> EAST_AABB[age]
            Direction.SOUTH -> SOUTH_AABB[age]
            Direction.WEST -> WEST_AABB[age]
            else -> NORTH_AABB[age]
        }
    }

    companion object {

        val AGE: IntProperty = Properties.AGE_2
        const val MAX_AGE = 2
        const val MIN_AGE = 0
        // We have different dimensions from the cocoa beans
        private val NORTH_AABB = arrayOf(
            Block.createCuboidShape(6.0, 7.0, 0.0, 10.0, 11.0, 4.0),
            Block.createCuboidShape(5.5, 6.0, 0.0, 10.5, 11.0, 5.0),
            Block.createCuboidShape(5.0, 3.0, 0.0, 11.0, 9.0, 6.0)
        )
        private val SOUTH_AABB = arrayOf(
            Block.createCuboidShape(6.0, 7.0, 12.0, 10.0, 11.0, 16.0),
            Block.createCuboidShape(5.5, 6.0, 11.0, 10.5, 11.0, 16.0),
            Block.createCuboidShape(5.0, 3.0, 10.0, 11.0, 9.0, 16.0)
        )
        private val EAST_AABB = arrayOf(
            Block.createCuboidShape(12.0, 7.0, 6.0, 16.0, 11.0, 10.0),
            Block.createCuboidShape(11.0, 6.0, 5.5, 16.0, 11.0, 10.5),
            Block.createCuboidShape(10.0, 3.0, 5.0, 16.0, 9.0, 11.0)
        )
        private val WEST_AABB = arrayOf(
            Block.createCuboidShape(0.0, 7.0, 6.0, 4.0, 11.0, 10.0),
            Block.createCuboidShape(0.0, 6.0, 5.5, 5.0, 11.0, 10.5),
            Block.createCuboidShape(0.0, 3.0, 5.0, 6.0, 9.0, 11.0)
        )
    }


}