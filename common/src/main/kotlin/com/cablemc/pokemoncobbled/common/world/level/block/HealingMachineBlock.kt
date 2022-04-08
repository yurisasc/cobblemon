package com.cablemc.pokemoncobbled.common.world.level.block

import com.cablemc.pokemoncobbled.common.CobbledBlockEntities
import com.cablemc.pokemoncobbled.common.api.text.green
import com.cablemc.pokemoncobbled.common.api.text.red
import com.cablemc.pokemoncobbled.common.api.text.yellow
import com.cablemc.pokemoncobbled.common.util.lang
import com.cablemc.pokemoncobbled.common.util.party
import com.cablemc.pokemoncobbled.common.util.sendServerMessage
import com.cablemc.pokemoncobbled.common.world.level.block.entity.HealingMachineBlockEntity
import net.minecraft.ChatFormatting
import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class HealingMachineBlock(properties: Properties) : BaseEntityBlock(properties) {
    companion object {
        val NORTH_AABB = box(1.5, 0.0, 0.0, 14.5, 12.0, 16.0)
        val SOUTH_AABB = box(1.5, 0.0, 0.0, 14.5, 12.0, 16.0)
        val WEST_AABB = box(0.0, 0.0, 1.5, 16.0, 12.0, 14.5)
        val EAST_AABB = box(0.0, 0.0, 1.5, 16.0, 12.0, 14.5)
    }

    init {
        this.registerDefaultState(this.stateDefinition.any().setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH))
    }

    override fun getShape(blockState: BlockState, blockGetter: BlockGetter, blockPos: BlockPos, collisionContext: CollisionContext): VoxelShape {
        return when (blockState.getValue(HorizontalDirectionalBlock.FACING)) {
            Direction.SOUTH -> SOUTH_AABB
            Direction.WEST -> WEST_AABB
            Direction.EAST -> EAST_AABB
            else -> NORTH_AABB
        }
    }

    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity {
        return HealingMachineBlockEntity(blockPos, blockState)
    }

    override fun getStateForPlacement(blockPlaceContext: BlockPlaceContext): BlockState {
        val direction = blockPlaceContext.horizontalDirection
        return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, direction)
    }

    override fun isPathfindable(blockState: BlockState, blockGetter: BlockGetter, blockPos: BlockPos, pathComputationType: PathComputationType): Boolean {
        return false
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(HorizontalDirectionalBlock.FACING)
    }

    override fun rotate(blockState: BlockState, rotation: Rotation): BlockState {
        return blockState.setValue(HorizontalDirectionalBlock.FACING, rotation.rotate(blockState.getValue(HorizontalDirectionalBlock.FACING)))
    }

    override fun mirror(blockState: BlockState, mirror: Mirror): BlockState {
        return blockState.rotate(mirror.getRotation(blockState.getValue(HorizontalDirectionalBlock.FACING)))
    }

    override fun use(blockState: BlockState, level: Level, blockPos: BlockPos, player: Player, interactionHand: InteractionHand, blockHitResult: BlockHitResult): InteractionResult {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS
        }

        val blockEntity = level.getBlockEntity(blockPos)
        if(blockEntity !is HealingMachineBlockEntity) {
            return InteractionResult.SUCCESS
        }

        if(blockEntity.isInUse) {
            player.sendServerMessage(lang("healingmachine.alreadyinuse").red())
            return InteractionResult.SUCCESS
        }

        val serverPlayer = player as ServerPlayer
        val party = serverPlayer.party()
        if(party.getAll().isEmpty()) {
            player.sendServerMessage(lang("healingmachine.nopokemon").red())
            return InteractionResult.SUCCESS
        }

        if(party.getHealingRemainderPercent() == 0.0f) {
            player.sendServerMessage(lang("healingmachine.alreadyhealed").red())
            return InteractionResult.SUCCESS
        }

        if(blockEntity.canHeal(player)) {
            blockEntity.activate(player)
            player.sendServerMessage(lang("healingmachine.healing").green())
        } else {
            val neededCharge = player.party().getHealingRemainderPercent() - blockEntity.healingCharge
            player.sendServerMessage(lang("healingmachine.notenoughcharge", "${((neededCharge/party.getAll().size)*100f).toInt()}%").red())
        }
        return InteractionResult.CONSUME
    }

    override fun <T : BlockEntity> getTicker(level: Level, blockState: BlockState, blockEntityType: BlockEntityType<T>): BlockEntityTicker<T>? {
        if(blockEntityType != CobbledBlockEntities.HEALING_MACHINE.get()) {
            return null
        }
        return HealingMachineBlockEntity.Companion as BlockEntityTicker<T>
    }

    override fun getRenderShape(blockState: BlockState): RenderShape {
        return RenderShape.MODEL
    }
}