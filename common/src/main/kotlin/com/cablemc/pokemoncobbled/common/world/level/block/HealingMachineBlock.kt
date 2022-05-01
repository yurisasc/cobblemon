package com.cablemc.pokemoncobbled.common.world.level.block

import com.cablemc.pokemoncobbled.common.CobbledBlockEntities
import com.cablemc.pokemoncobbled.common.api.text.green
import com.cablemc.pokemoncobbled.common.api.text.red
import com.cablemc.pokemoncobbled.common.util.lang
import com.cablemc.pokemoncobbled.common.util.party
import com.cablemc.pokemoncobbled.common.util.sendServerMessage
import com.cablemc.pokemoncobbled.common.world.level.block.entity.HealingMachineBlockEntity
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.state.StateManager
import net.minecraft.util.ActionResult
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World

class HealingMachineBlock(properties: Settings) : BlockWithEntity(properties) {
    companion object {
        val NORTH_AABB = Block.createCuboidShape(1.5, 0.0, 0.0, 14.5, 12.0, 16.0)
        val SOUTH_AABB = Block.createCuboidShape(1.5, 0.0, 0.0, 14.5, 12.0, 16.0)
        val WEST_AABB = Block.createCuboidShape(0.0, 0.0, 1.5, 16.0, 12.0, 14.5)
        val EAST_AABB = Block.createCuboidShape(0.0, 0.0, 1.5, 16.0, 12.0, 14.5)
    }

    init {
        defaultState = this.stateManager.defaultState.with(HorizontalFacingBlock.FACING, Direction.NORTH)
    }

    @Deprecated("Deprecated in Java")
    override fun getCollisionShape(blockState: BlockState, blockGetter: BlockView, blockPos: BlockPos, collisionContext: ShapeContext): VoxelShape {
        return when (blockState.get(HorizontalFacingBlock.FACING)) {
            Direction.SOUTH -> SOUTH_AABB
            Direction.WEST -> WEST_AABB
            Direction.EAST -> EAST_AABB
            else -> NORTH_AABB
        }
    }

    override fun createBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity {
        return HealingMachineBlockEntity(blockPos, blockState)
    }

    override fun getPlacementState(blockPlaceContext: ItemPlacementContext): BlockState {
        return this.defaultState.with(HorizontalFacingBlock.FACING, blockPlaceContext.playerFacing)
    }

    @Deprecated("Deprecated in Java")
    override fun canPathfindThrough(blockState: BlockState, blockGetter: BlockView, blockPos: BlockPos, pathComputationType: NavigationType): Boolean {
        return false
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(HorizontalFacingBlock.FACING)
    }

    @Deprecated("Deprecated in Java")
    override fun rotate(blockState: BlockState, rotation: BlockRotation): BlockState {
        return blockState.with(HorizontalFacingBlock.FACING, rotation.rotate(blockState.get(HorizontalFacingBlock.FACING)))
    }

    @Deprecated("Deprecated in Java")
    override fun mirror(blockState: BlockState, mirror: BlockMirror): BlockState {
        return blockState.rotate(mirror.getRotation(blockState.get(HorizontalFacingBlock.FACING)))
    }

    @Deprecated("Deprecated in Java")
    override fun onUse(blockState: BlockState, world: World, blockPos: BlockPos, player: PlayerEntity, interactionHand: Hand, blockHitResult: BlockHitResult): ActionResult {
        if (world.isClient) {
            return ActionResult.SUCCESS
        }

        val BlockWithEntity = world.getBlockEntity(blockPos)
        if (BlockWithEntity !is HealingMachineBlockEntity) {
            return ActionResult.SUCCESS
        }

        if (BlockWithEntity.isInUse) {
            player.sendServerMessage(lang("healingmachine.alreadyinuse").red())
            return ActionResult.SUCCESS
        }

        val ServerPlayerEntity = player as ServerPlayerEntity
        val party = ServerPlayerEntity.party()
        if (party.none()) {
            player.sendServerMessage(lang("healingmachine.nopokemon").red())
            return ActionResult.SUCCESS
        }

        if (party.getHealingRemainderPercent() == 0.0f) {
            player.sendServerMessage(lang("healingmachine.alreadyhealed").red())
            return ActionResult.SUCCESS
        }

        if (BlockWithEntity.canHeal(player)) {
            BlockWithEntity.activate(player)
            player.sendServerMessage(lang("healingmachine.healing").green())
        } else {
            val neededCharge = player.party().getHealingRemainderPercent() - BlockWithEntity.healingCharge
            player.sendServerMessage(lang("healingmachine.notenoughcharge", "${((neededCharge/party.count())*100f).toInt()}%").red())
        }
        return ActionResult.CONSUME
    }

    override fun <T : BlockEntity> getTicker(world: World, blockState: BlockState, BlockWithEntityType: BlockEntityType<T>): BlockEntityTicker<T>? {
        if (BlockWithEntityType != CobbledBlockEntities.HEALING_MACHINE.get()) {
            return null
        }
        return HealingMachineBlockEntity.Companion as BlockEntityTicker<T>
    }

    @Deprecated("Deprecated in Java")
    override fun getRenderType(blockState: BlockState): BlockRenderType {
        return BlockRenderType.MODEL
    }
}