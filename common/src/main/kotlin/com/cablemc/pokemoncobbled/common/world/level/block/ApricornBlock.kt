package com.cablemc.pokemoncobbled.common.world.level.block

import com.cablemc.pokemoncobbled.common.CobbledBlocks
import com.cablemc.pokemoncobbled.common.item.ApricornItem
import net.minecraft.block.*
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import net.minecraft.world.WorldView
import java.util.*
import java.util.function.Supplier

class ApricornBlock(properties: Settings, val itemSupplier: Supplier<ApricornItem>) : HorizontalFacingBlock(properties), Fertilizable {

    companion object {
        val AGE = Properties.AGE_2
        val EAST_AABB = arrayOf(
            Block.createCuboidShape(12.0, 7.0, 6.0, 16.0, 11.0, 10.0),
            Block.createCuboidShape(11.0, 6.0, 5.5, 16.0, 11.0, 10.5),
            Block.createCuboidShape(10.0, 3.0, 5.0, 16.0, 9.0, 11.0)
        )
        val WEST_AABB = arrayOf(
            Block.createCuboidShape(0.0, 7.0, 6.0, 4.0, 11.0, 10.0),
            Block.createCuboidShape(0.0, 6.0, 5.5, 5.0, 11.0, 10.5),
            Block.createCuboidShape(0.0, 3.0, 5.0, 6.0, 9.0, 11.0)
        )
        val NORTH_AABB = arrayOf(
            Block.createCuboidShape(6.0, 7.0, 0.0, 10.0, 11.0, 4.0),
            Block.createCuboidShape(5.5, 6.0, 0.0, 10.5, 11.0, 5.0),
            Block.createCuboidShape(5.0, 3.0, 0.0, 11.0, 9.0, 6.0)
        )
        val SOUTH_AABB = arrayOf(
            Block.createCuboidShape(6.0, 7.0, 12.0, 10.0, 11.0, 16.0),
            Block.createCuboidShape(5.5, 6.0, 11.0, 10.5, 11.0, 16.0),
            Block.createCuboidShape(5.0, 3.0, 10.0, 11.0, 9.0, 16.0)
        )
    }

    init {
        this.defaultState = this.stateManager.defaultState.with(FACING, Direction.NORTH).with(AGE, 0)
    }

    @Deprecated("Deprecated in Java")
    override fun onUse(blockState: BlockState, world: World, blockPos: BlockPos, player: PlayerEntity, interactionHand: Hand, blockHitResult: BlockHitResult): ActionResult {
        val age = blockState.get(AGE)
        val fullyGrown = age == 2
        return if (!fullyGrown && player.getStackInHand(interactionHand).isOf(Items.BONE_MEAL)) {
            ActionResult.PASS
        } else if (fullyGrown) {
            Block.dropStack(world, blockPos, ItemStack(itemSupplier.get()))
            world.setBlockState(blockPos, blockState.with(AGE, 0), 2)
            ActionResult.success(world.isClient)
        } else {
            super.onUse(blockState, world, blockPos, player, interactionHand, blockHitResult)
        }
    }

    override fun hasRandomTicks(blockState: BlockState): Boolean {
        return blockState.get(AGE) < 2
    }

    @Deprecated("Deprecated in Java")
    override fun randomTick(blockState: BlockState, serverLevel: ServerWorld, blockPos: BlockPos, random: Random) {
        val age = blockState.get(AGE)
        if (age < 3 && serverLevel.random.nextInt(5) == 0) {
            serverLevel.setBlockState(blockPos, blockState.with(AGE, age + 1), 2)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun canPlaceAt(blockState: BlockState, levelReader: WorldView, blockPos: BlockPos): Boolean {
        val relativeState = levelReader.getBlockState(blockPos.offset(blockState.get(FACING)))
        return relativeState.block == CobbledBlocks.APRICORN_LEAVES.get()
    }

    @Deprecated("Deprecated in Java")
    override fun getStateForNeighborUpdate(blockState: BlockState, direction: Direction, arg3: BlockState, world: WorldAccess, neighborBlockPos: BlockPos, arg6: BlockPos): BlockState? {
        return if (direction == blockState.get(FACING) && !blockState.canPlaceAt(world, neighborBlockPos)) {
            Blocks.AIR.defaultState
        } else {
            super.getStateForNeighborUpdate(blockState, direction, arg3, world, neighborBlockPos, arg6)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun getCollisionShape(blockState: BlockState, blockGetter: BlockView, blockPos: BlockPos, collisionContext: ShapeContext) = this.resolveShape(blockState)

    @Deprecated("Deprecated in Java")
    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext) = this.resolveShape(state)

    override fun isFertilizable(blockGetter: BlockView, blockPos: BlockPos, blockState: BlockState, bl: Boolean): Boolean {
        return blockState.get(AGE) < 2
    }

    override fun canGrow(world: World, random: Random, blockPos: BlockPos, blockState: BlockState): Boolean {
        return true
    }

    override fun grow(serverLevel: ServerWorld, random: Random, blockPos: BlockPos, blockState: BlockState) {
        serverLevel.setBlockState(blockPos, blockState.with(AGE, blockState.get(AGE) + 1), 3);
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING, AGE)
    }

    @Deprecated("Deprecated in Java", ReplaceWith("false"))
    override fun canPathfindThrough(blockState: BlockState, blockGetter: BlockView, blockPos: BlockPos, pathComputingType: NavigationType): Boolean {
        return false
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState = this.defaultState.with(FACING, ctx.playerLookDirection.opposite)

    private fun resolveShape(state: BlockState): VoxelShape {
        val age = state.get(AGE)
        return when (state.get(FACING)) {
            Direction.NORTH -> NORTH_AABB[age]
            Direction.EAST -> EAST_AABB[age]
            Direction.SOUTH -> SOUTH_AABB[age]
            Direction.WEST -> WEST_AABB[age]
            else -> NORTH_AABB[age]
        }
    }

}