/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.api.tags.CobblemonItemTags
import com.cobblemon.mod.common.block.SaccharineStrippedLogBlock.Companion
import net.minecraft.block.*
import net.minecraft.block.BeehiveBlock.HONEY_LEVEL
import net.minecraft.entity.*
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.Fluids
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.particle.ParticleTypes
import net.minecraft.registry.tag.BlockTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.IntProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import net.minecraft.util.Arm
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.*

@Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
class SaccharineLeafBlock(settings: Settings) : LeavesBlock(settings), Fertilizable {

    init {
        this.defaultState = this.stateManager.defaultState
            .with(AGE, MIN_AGE)
            .with(DISTANCE, DISTANCE_MAX)
            .with(PERSISTENT, false)
            .with(Properties.WATERLOGGED, false)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        val blockState = defaultState
        val worldView = ctx.world
        val blockPos = ctx.blockPos
        return if (blockState.canPlaceAt(worldView, blockPos)) {
            blockState.with(PERSISTENT, true).with(Properties.WATERLOGGED, worldView.getFluidState(blockPos).isOf(Fluids.WATER))
        } else null
    }

    override fun getStateForNeighborUpdate(state: BlockState, direction: Direction, neighborState: BlockState, world: WorldAccess, pos: BlockPos, neighborPos: BlockPos): BlockState? {
        return if (!state.canPlaceAt(world, pos)) {
            Blocks.AIR.defaultState
        } else {
            super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos)
        }
    }

    override fun hasRandomTicks(state: BlockState) = state.get(AGE) != 2

    override fun randomTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        // todo if a honey soaked leaf block is the first block below this block then over time age that leaf block
        if (random.nextInt(180) == 0) {

            // todo if leaf block is within 10 blocks of a lead soaked block then age it
            for (i in 1..10) {

                val abovePos = pos.up(i)
                val aboveState = world.getBlockState(abovePos)

                if (!aboveState.isAir) {
                    if ((aboveState.block is SaccharineLeafBlock && aboveState.get(AGE) == 2) ||
                            (aboveState.block is BeehiveBlock && aboveState.get(HONEY_LEVEL) == 5)) {
                        // todo remove age from top block
                        world.setBlockState(abovePos, aboveState.with(AGE, 0), 2)

                        // todo age the leaf bottom block
                        world.setBlockState(pos, state.with(AGE, 2), 2)
                    }
                    break
                }
            }
        }

        super.randomTick(state,  world, pos, random)
    }


    @Deprecated("Deprecated in Java")
    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        return SHAPE
    }

    // todo make block
    @Deprecated("Deprecated in Java")
    override fun getCollisionShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        if (context is EntityShapeContext && (context.entity as? ItemEntity)?.stack?.isIn(CobblemonItemTags.APRICORNS) == true) {
            return VoxelShapes.empty()
        }
        return super.getCollisionShape(state, world, pos, context)
    }

    override fun isFertilizable(world: WorldView, pos: BlockPos, state: BlockState, isClient: Boolean) = false /*state.get(AGE) < MAX_AGE*/

    override fun canGrow(world: World, random: Random, pos: BlockPos, state: BlockState) = true

    override fun grow(world: ServerWorld, random: Random, pos: BlockPos, state: BlockState) {
        world.setBlockState(pos, state.with(AGE, state.get(AGE) + 1), 2)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(AGE, DISTANCE, PERSISTENT, Properties.WATERLOGGED)
    }

    @Deprecated("Deprecated in Java")
    override fun randomDisplayTick(state: BlockState, world: World, pos: BlockPos, random: Random) {

        if (state.get(AGE) == 2) {
            for (i in 0 until random.nextInt(1) + 1) {
                this.spawnHoneyParticles(world, pos, state)
            }
        }

        // this code was for them aging as time goes on
        /* if (world.random.nextInt(5) == 0) {

            val currentAge = state.get(AGE)
            if (currentAge < MAX_AGE) {
                world.setBlockState(pos, state.with(AGE, currentAge + 1), 2)
            }
        }*/
    }

    /*@Deprecated("Deprecated in Java")
    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        val blockState = world.getBlockState(pos.down())
        return blockState.isIn(CobblemonBlockTags.SACCHARINE_LEAVES)
    }*/

    private fun spawnHoneyParticles(world: World, pos: BlockPos, state: BlockState) {
        if (state.fluidState.isEmpty && !(world.random.nextFloat() < 0.3f)) {
            val voxelShape = state.getCollisionShape(world, pos)
            val d = voxelShape.getMax(Direction.Axis.Y)
            if (d >= 1.0 && !state.isIn(BlockTags.IMPERMEABLE)) {
                val e = voxelShape.getMin(Direction.Axis.Y)
                if (e > 0.0) {
                    this.addHoneyParticle(world, pos, voxelShape, pos.y.toDouble() + e - 0.05)
                } else {
                    val blockPos = pos.down()
                    val blockState = world.getBlockState(blockPos)
                    val voxelShape2 = blockState.getCollisionShape(world, blockPos)
                    val f = voxelShape2.getMax(Direction.Axis.Y)
                    if ((f < 1.0 || !blockState.isFullCube(world, blockPos)) && blockState.fluidState.isEmpty) {
                        this.addHoneyParticle(world, pos, voxelShape, pos.y.toDouble() - 0.05)
                    }
                }
            }
        }
    }

    private fun addHoneyParticle(world: World, pos: BlockPos, shape: VoxelShape, height: Double) {
        this.addHoneyParticle(
            world, pos.x.toDouble() + shape.getMin(Direction.Axis.X), pos.x.toDouble() + shape.getMax(
                Direction.Axis.X
            ), pos.z.toDouble() + shape.getMin(Direction.Axis.Z), pos.z.toDouble() + shape.getMax(
                Direction.Axis.Z
            ), height
        )
    }

    private fun addHoneyParticle(world: World, minX: Double, maxX: Double, minZ: Double, maxZ: Double, height: Double) {
        world.addParticle(
            ParticleTypes.DRIPPING_HONEY,
            MathHelper.lerp(world.random.nextDouble(), minX, maxX),
            height,
            MathHelper.lerp(world.random.nextDouble(), minZ, maxZ),
            0.0,
            0.0,
            0.0
        )
    }

    override fun canPathfindThrough(state: BlockState, world: BlockView, pos: BlockPos, type: NavigationType) = false

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        // todo if item in players hand is glass bottle and AGE is more than 0
        if (player.getStackInHand(hand).isOf(Items.GLASS_BOTTLE) && state.get(AGE) > 0)
        {
            // decrement stack if not in creative mode
            if (!player.isCreative)
                player.getStackInHand(hand).decrement(1)

            // give player honey bottle for now
            player.giveItemStack(Items.HONEY_BOTTLE.defaultStack)

            // todo reset AGE
            world.setBlockState(pos, state.with(AGE, 0), 2)

            val currentAge = state.get(AGE)
        } else if (player.getStackInHand(hand).isOf(Items.HONEY_BOTTLE) && state.get(AGE) != 2) {
            // decrement stack if not in creative mode
            if (!player.isCreative)
                player.getStackInHand(hand).decrement(1)

            // todo set age to 2
            world.setBlockState(pos, state.with(AGE, 2), 2)
        }

        if (state.get(AGE) != MAX_AGE) {
            return super.onUse(state, world, pos, player, hand, hit)
        }

        //doHarvest(world, state, pos, player)
        return ActionResult.SUCCESS
    }

    /*override fun onBlockBreakStart(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity) {
        if (state.get(AGE) != MAX_AGE) {
            return super.onBlockBreakStart(state, world, pos, player)
        }

        //doHarvest(world, state, pos, player)
    }*/

    companion object {

        val AGE: IntProperty = Properties.AGE_2
        val DISTANCE: IntProperty = Properties.DISTANCE_1_7
        val PERSISTENT: BooleanProperty = Properties.PERSISTENT
        const val MAX_AGE = Properties.AGE_2_MAX
        const val MIN_AGE = 0
        const val DISTANCE_MAX = 7

        private val SHAPE: VoxelShape = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
    }

}
