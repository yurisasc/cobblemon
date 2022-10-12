/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.world.block

import com.cablemc.pokemod.common.api.text.green
import com.cablemc.pokemod.common.api.text.red
import com.cablemc.pokemod.common.util.lang
import com.cablemc.pokemod.common.util.party
import com.cablemc.pokemod.common.world.block.entity.HealingMachineBlockEntity
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
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

        val blockEntity = world.getBlockEntity(blockPos)
        if (blockEntity !is HealingMachineBlockEntity) {
            return ActionResult.SUCCESS
        }

        if (blockEntity.isInUse) {
            player.sendMessage(lang("healingmachine.alreadyinuse").red())
            return ActionResult.SUCCESS
        }

        val serverPlayerEntity = player as ServerPlayerEntity
        val party = serverPlayerEntity.party()
        if (party.none()) {
            player.sendMessage(lang("healingmachine.nopokemon").red())
            return ActionResult.SUCCESS
        }

        if (party.getHealingRemainderPercent() == 0.0f) {
            player.sendMessage(lang("healingmachine.alreadyhealed").red())
            return ActionResult.SUCCESS
        }

        if (blockEntity.canHeal(player)) {
            blockEntity.activate(player)
            player.sendMessage(lang("healingmachine.healing").green())
        } else {
            val neededCharge = player.party().getHealingRemainderPercent() - blockEntity.healingCharge
            player.sendMessage(lang("healingmachine.notenoughcharge", "${((neededCharge/party.count())*100f).toInt()}%").red())
        }
        return ActionResult.CONSUME
    }

    override fun onPlaced(world: World, blockPos: BlockPos, blockState: BlockState, livingEntity: LivingEntity?, itemStack: ItemStack) {
        super.onPlaced(world, blockPos, blockState, livingEntity, itemStack)

        if (!world.isClient && livingEntity is ServerPlayerEntity && livingEntity.isCreative) {
            val blockEntity = world.getBlockEntity(blockPos)
            if (blockEntity !is HealingMachineBlockEntity) {
                return
            }
            blockEntity.infinite = true
        }
    }

    override fun <T : BlockEntity> getTicker(world: World, blockState: BlockState, BlockWithEntityType: BlockEntityType<T>): BlockEntityTicker<T>? {
        if (BlockWithEntityType != com.cablemc.pokemod.common.PokemodBlockEntities.HEALING_MACHINE.get()) {
            return null
        }
        return HealingMachineBlockEntity.Companion as BlockEntityTicker<T>
    }

    @Deprecated("Deprecated in Java")
    override fun getRenderType(blockState: BlockState): BlockRenderType {
        return BlockRenderType.MODEL
    }
}