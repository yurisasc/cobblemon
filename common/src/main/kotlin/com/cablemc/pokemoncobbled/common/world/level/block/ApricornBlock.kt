package com.cablemc.pokemoncobbled.common.world.level.block

import com.cablemc.pokemoncobbled.common.CobbledBlocks
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.BonemealableBlock
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import java.util.*
import java.util.function.Supplier

class ApricornBlock(properties: Properties, val itemSupplier: Supplier<Item>) : HorizontalDirectionalBlock(properties), BonemealableBlock {

    companion object {
        val AGE = BlockStateProperties.AGE_2
        val EAST_AABB = arrayOf(Block.box(12.0, 7.0, 6.0, 16.0, 11.0, 10.0), Block.box(11.0, 6.0, 5.5, 16.0, 11.0, 10.5), Block.box(10.0, 3.0, 5.0, 16.0, 9.0, 11.0))
        val WEST_AABB = arrayOf(Block.box(0.0, 7.0, 6.0, 4.0, 11.0, 10.0), Block.box(0.0, 6.0, 5.5, 5.0, 11.0, 10.5), Block.box(0.0, 3.0, 5.0, 6.0, 9.0, 11.0))
        val NORTH_AABB = arrayOf(Block.box(6.0, 7.0, 0.0, 10.0, 11.0, 4.0), Block.box(5.5, 6.0, 0.0, 10.5, 11.0, 5.0), Block.box(5.0, 3.0, 0.0, 11.0, 9.0, 6.0))
        val SOUTH_AABB = arrayOf(Block.box(6.0, 7.0, 12.0, 10.0, 11.0, 16.0), Block.box(5.5, 6.0, 11.0, 10.5, 11.0, 16.0), Block.box(5.0, 3.0, 10.0, 11.0, 9.0, 16.0))
    }

    init {
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(AGE, 0))
    }

    override fun use(blockState: BlockState, level: Level, blockPos: BlockPos, player: Player, interactionHand: InteractionHand, blockHitResult: BlockHitResult): InteractionResult {
        val age = blockState.getValue(AGE)
        val fullyGrown = age == 2
        return if(!fullyGrown && player.getItemInHand(interactionHand).`is`(Items.BONE_MEAL)) {
            InteractionResult.PASS
        } else if(fullyGrown) {
            Block.popResource(level, blockPos, ItemStack(itemSupplier.get()))
            level.setBlock(blockPos, blockState.setValue(AGE, 0), 2)
            InteractionResult.sidedSuccess(level.isClientSide)
        } else {
            super.use(blockState, level, blockPos, player, interactionHand, blockHitResult)
        }
    }

    override fun isRandomlyTicking(blockState: BlockState): Boolean {
        return blockState.getValue(AGE) < 2
    }

    override fun randomTick(blockState: BlockState, serverLevel: ServerLevel, blockPos: BlockPos, random: Random) {
        val age = blockState.getValue(AGE)
        if (age < 3 && serverLevel.random.nextInt(5) == 0) {
            serverLevel.setBlock(blockPos, blockState.setValue(AGE, age + 1), 2)
        }
    }

    override fun canSurvive(blockState: BlockState, levelReader: LevelReader, blockPos: BlockPos): Boolean {
        val relativeState = levelReader.getBlockState(blockPos.relative(blockState.getValue(FACING)))
        return relativeState.block == CobbledBlocks.APRICORN_LEAVES.get()
    }

    override fun getShape(blockState: BlockState, blockGetter: BlockGetter, blockPos: BlockPos, collisionContext: CollisionContext): VoxelShape {
        val age = blockState.getValue(AGE)
        return when(blockState.getValue(FACING)) {
            Direction.NORTH -> NORTH_AABB[age]
            Direction.EAST -> EAST_AABB[age]
            Direction.SOUTH -> SOUTH_AABB[age]
            Direction.WEST -> WEST_AABB[age]
            else -> NORTH_AABB[age]
        }
    }

    override fun isValidBonemealTarget(blockGetter: BlockGetter, blockPos: BlockPos, blockState: BlockState, bl: Boolean): Boolean {
        return blockState.getValue(AGE) < 2
    }

    override fun isBonemealSuccess(level: Level, random: Random, blockPos: BlockPos, blockState: BlockState): Boolean {
        return true
    }

    override fun performBonemeal(serverLevel: ServerLevel, random: Random, blockPos: BlockPos, blockState: BlockState) {
        serverLevel.setBlock(blockPos, blockState.setValue(AGE, blockState.getValue(AGE) + 1), 3);
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING, AGE)
    }

    override fun isPathfindable(blockState: BlockState, blockGetter: BlockGetter, blockPos: BlockPos, pathComputingType: PathComputationType): Boolean {
        return false
    }
    
}