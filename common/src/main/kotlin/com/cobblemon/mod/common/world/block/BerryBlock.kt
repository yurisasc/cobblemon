/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.world.block

import com.cobblemon.mod.common.api.berry.Berries
import com.cobblemon.mod.common.api.berry.Berry
import com.cobblemon.mod.common.tags.CobblemonBlockTags
import com.cobblemon.mod.common.world.block.entity.BerryBlockEntity
import net.minecraft.block.*
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.IntProperty
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import net.minecraft.world.WorldView

class BerryBlock(private val berryIdentifier: Identifier, settings: Settings) : BlockWithEntity(settings), Fertilizable {

    fun berry(): Berry? = Berries.getByIdentifier(this.berryIdentifier)

    override fun createBlockEntity(pos: BlockPos, state: BlockState) = BerryBlockEntity(pos, state)

    override fun isFertilizable(world: BlockView, pos: BlockPos, state: BlockState, isClient: Boolean) = !this.isMaxAge(state)

    override fun canGrow(world: World, random: Random, pos: BlockPos, state: BlockState) = !this.isMaxAge(state)

    override fun hasRandomTicks(state: BlockState) = !this.isMaxAge(state)

    @Deprecated("Deprecated in Java")
    override fun randomTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        if (world.random.nextInt(5) == 0) {
            val currentAge = state.get(AGE)
            if (currentAge < MAX_AGE) {
                world.setBlockState(pos, state.with(AGE, currentAge + 1), 2)
            }
        }
    }

    override fun grow(world: ServerWorld, random: Random, pos: BlockPos, state: BlockState) {
        world.setBlockState(pos, state.with(AGE, state.get(AGE) + 1), 2)
    }

    @Deprecated("Deprecated in Java")
    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        if (player.getStackInHand(hand).isOf(Items.BONE_MEAL)) {
            return ActionResult.PASS
        }
        else if (this.isMaxAge(state)) {
            val blockEntity = world.getBlockEntity(pos) as? BerryBlockEntity ?: return ActionResult.PASS
            blockEntity.harvest(world, state, pos, player).forEach { drop ->
                Block.dropStack(world, pos, drop)
            }
            return ActionResult.success(world.isClient)
        }
        return super.onUse(state, world, pos, player, hand, hit)
    }

    @Deprecated("Deprecated in Java")
    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        return world.getBlockState(pos.down()).isIn(CobblemonBlockTags.BERRY_SOIL)
    }

    @Deprecated("Deprecated in Java")
    override fun getStateForNeighborUpdate(state: BlockState, direction: Direction, neighborState: BlockState, world: WorldAccess, pos: BlockPos, neighborPos: BlockPos): BlockState {
        return if (state.canPlaceAt(world, pos)) super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos) else Blocks.AIR.defaultState
    }

    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack) {
        if (!world.isClient) {
            val blockEntity = world.getBlockEntity(pos) as? BerryBlockEntity ?: return
            blockEntity.generateGrowthPoints(world, state, pos, placer)
        }
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(AGE)
    }

    override fun getPickStack(world: BlockView?, pos: BlockPos?, state: BlockState?): ItemStack {
        val berryItem = this.berry()?.item() ?: return ItemStack.EMPTY
        return ItemStack(berryItem)
    }

    @Deprecated("Deprecated in Java")
    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        val berry = this.berry() ?: return VoxelShapes.fullCube()
        return when(state.get(AGE)) {
            MAX_AGE -> VoxelShapes.union(berry.matureShape, berry.shapeAt(0, false))
            FLOWER_AGE -> VoxelShapes.union(berry.matureShape, berry.shapeAt(0, true))
            MATURE_AGE -> berry.matureShape
            else -> berry.sproutShape
        }
    }

    private fun isMaxAge(state: BlockState) = state.get(AGE) == MAX_AGE

    companion object {

        private const val MATURE_AGE = 3
        private const val FLOWER_AGE = 4
        private const val MAX_AGE = 5
        val AGE = IntProperty.of("age", 0, MAX_AGE)

    }
}