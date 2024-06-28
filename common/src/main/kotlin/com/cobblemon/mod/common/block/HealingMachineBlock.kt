/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.api.text.gray
import com.cobblemon.mod.common.api.text.green
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.block.entity.HealingMachineBlockEntity
import com.cobblemon.mod.common.util.*
import com.mojang.serialization.MapCodec
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.world.entity.player.Player
import net.minecraft.item.Item
import net.minecraft.item.ItemPlacementContext
import net.minecraft.world.item.ItemStack
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.level.ServerPlayer
import net.minecraft.state.StateManager
import net.minecraft.state.property.IntProperty
import net.minecraft.state.property.Property
import net.minecraft.network.chat.Component
import net.minecraft.util.ActionResult
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.core.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.pathfinder.PathComputationType

@Suppress("DEPRECATED", "OVERRIDE_DEPRECATION")
class HealingMachineBlock(properties: Settings) : BaseEntityBlock(properties) {
    companion object {
        val CODEC: MapCodec<HealingMachineBlock> = createCodec(::HealingMachineBlock)

        private val NORTH_SOUTH_AABB = VoxelShapes.union(
            VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 0.625, 1.0),
            VoxelShapes.cuboid(0.0625, 0.625, 0.0, 0.9375, 0.875, 0.125),
            VoxelShapes.cuboid(0.0625, 0.625, 0.875, 0.9375, 0.875, 1.0),
            VoxelShapes.cuboid(0.0625, 0.625, 0.125, 0.1875, 0.75, 0.875),
            VoxelShapes.cuboid(0.8125, 0.625, 0.125, 0.9375, 0.75, 0.875),
            VoxelShapes.cuboid(0.1875, 0.625, 0.125, 0.8125, 0.6875, 0.875)
        )

        private val WEST_EAST_AABB = VoxelShapes.union(
            VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 0.625, 1.0),
            VoxelShapes.cuboid(0.875, 0.625, 0.0625, 1.0, 0.875, 0.9375),
            VoxelShapes.cuboid(0.0, 0.625, 0.0625, 0.125, 0.875, 0.9375),
            VoxelShapes.cuboid(0.125, 0.625, 0.0625, 0.875, 0.75, 0.1875),
            VoxelShapes.cuboid(0.125, 0.625, 0.8125, 0.875, 0.75, 0.9375),
            VoxelShapes.cuboid(0.125, 0.625, 0.1875, 0.875, 0.6875, 0.8125)
        )

        // Charge level 6 is used only when healing machine is active
        const val MAX_CHARGE_LEVEL = 5
        val CHARGE_LEVEL: IntProperty = IntProperty.of("charge", 0, MAX_CHARGE_LEVEL + 1)
    }

    init {
        defaultState = this.stateManager.defaultState
            .with(HorizontalFacingBlock.FACING, Direction.NORTH)
            .with(CHARGE_LEVEL, 0)
    }

    @Deprecated("Deprecated in Java")
    override fun getOutlineShape(blockState: BlockState, blockGetter: BlockView, blockPos: BlockPos, collisionContext: ShapeContext): VoxelShape {
        return when (blockState.get(HorizontalFacingBlock.FACING)) {
            Direction.WEST -> WEST_EAST_AABB
            Direction.EAST -> WEST_EAST_AABB
            else -> NORTH_SOUTH_AABB
        }
    }

    override fun createBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity {
        return HealingMachineBlockEntity(blockPos, blockState)
    }

    override fun getPlacementState(blockPlaceContext: ItemPlacementContext): BlockState {
        return this.defaultState.with(HorizontalFacingBlock.FACING, blockPlaceContext.horizontalPlayerFacing)
    }

    override fun getCodec(): MapCodec<out BaseEntityBlock> {
        return CODEC
    }

    override fun isPathfindable(
        blockState: BlockState,
        pathComputationType: PathComputationType
    ): Boolean {
        return false
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(HorizontalFacingBlock.FACING)
        builder.add(*arrayOf<Property<*>>(CHARGE_LEVEL))
    }

    override fun rotate(blockState: BlockState, rotation: BlockRotation): BlockState {
        return blockState.with(HorizontalFacingBlock.FACING, rotation.rotate(blockState.get(HorizontalFacingBlock.FACING)))
    }

    override fun mirror(blockState: BlockState, mirror: BlockMirror): BlockState {
        return blockState.rotate(mirror.getRotation(blockState.get(HorizontalFacingBlock.FACING)))
    }

    @Suppress("DEPRECATION")
    override fun onStateReplaced(state: BlockState, world: Level, pos: BlockPos?, newState: BlockState, moved: Boolean) {
        if (!state.isOf(newState.block)) super.onStateReplaced(state, world, pos, newState, moved)
    }

    override fun onUse(
        blockState: BlockState,
        world: Level,
        blockPos: BlockPos,
        player: Player,
        blockHitResult: BlockHitResult
    ): ActionResult {
        if (world.isClient) {
            return ActionResult.SUCCESS
        }

        val blockEntity = world.getBlockEntity(blockPos)
        if (blockEntity !is HealingMachineBlockEntity) {
            return ActionResult.SUCCESS
        }

        if (blockEntity.isInUse) {
            player.sendMessage(lang("healingmachine.alreadyinuse").red(), true)
            return ActionResult.SUCCESS
        }

        val serverPlayerEntity = player as ServerPlayer
        if (serverPlayerEntity.isInBattle()) {
            player.sendMessage(lang("healingmachine.inbattle").red(), true)
            return ActionResult.SUCCESS
        }
        val party = serverPlayerEntity.party()
        if (party.none()) {
            player.sendMessage(lang("healingmachine.nopokemon").red(), true)
            return ActionResult.SUCCESS
        }

        if (party.none { pokemon -> pokemon.canBeHealed() }) {
            player.sendMessage(lang("healingmachine.alreadyhealed").red(), true)
            return ActionResult.SUCCESS
        }

        if (HealingMachineBlockEntity.isUsingHealer(player)) {
            player.sendMessage(lang("healingmachine.alreadyhealing").red(), true)
            return ActionResult.SUCCESS
        }

        if (blockEntity.canHeal(player)) {
            blockEntity.activate(player)
            player.sendMessage(lang("healingmachine.healing").green(), true)
        } else {
            val neededCharge = player.party().getHealingRemainderPercent() - blockEntity.healingCharge
            player.sendMessage(lang("healingmachine.notenoughcharge", "${((neededCharge/party.count())*100f).toInt()}%").red(), true)
        }
        party.forEach { it.tryRecallWithAnimation() }
        return ActionResult.CONSUME
    }

    override fun onPlaced(world: Level, blockPos: BlockPos, blockState: BlockState, livingEntity: LivingEntity?, itemStack: ItemStack) {
        super.onPlaced(world, blockPos, blockState, livingEntity, itemStack)

        if (!world.isClient && livingEntity is ServerPlayer && livingEntity.isCreative) {
            val blockEntity = world.getBlockEntity(blockPos)
            if (blockEntity !is HealingMachineBlockEntity) {
                return
            }
            blockEntity.infinite = true
        }
    }

    override fun randomDisplayTick(state: BlockState, world: Level, pos: BlockPos, random: Random) {
        val blockEntity = world.getBlockEntity(pos)
        if (blockEntity !is HealingMachineBlockEntity) return

        if (random.nextInt(2) == 0 && blockEntity.healTimeLeft > 0) {
            val posX = pos.x.toDouble() + 0.5 + ((random.nextFloat() * 0.3F) * (if (random.nextInt(2) > 0) 1 else (-1))).toDouble()
            val posY = pos.y.toDouble() + 0.9
            val posZ = pos.z.toDouble() + 0.5 + ((random.nextFloat() * 0.3F) * (if (random.nextInt(2) > 0) 1 else (-1))).toDouble()
            world.addParticle(ParticleTypes.HAPPY_VILLAGER, posX, posY, posZ, 0.0, 0.0, 0.0)
        }
    }

    override fun hasComparatorOutput(state: BlockState) = true

    override fun getComparatorOutput(state: BlockState, world: Level, pos: BlockPos): Int = (world.getBlockEntity(pos) as? HealingMachineBlockEntity)?.currentSignal ?: 0

    override fun <T : BlockEntity> getTicker(world: Level, blockState: BlockState, blockWithEntityType: BlockEntityType<T>): BlockEntityTicker<T>? = validateTicker(blockWithEntityType, CobblemonBlockEntities.HEALING_MACHINE, HealingMachineBlockEntity.TICKER::tick)

    override fun getRenderShape(blockState: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun appendTooltip(
        stack: ItemStack,
        context: Item.TooltipContext,
        tooltip: MutableList<Component>,
        options: TooltipType
    ) {
        tooltip.add("block.${Cobblemon.MODID}.healing_machine.tooltip1".asTranslated().gray())
        tooltip.add("block.${Cobblemon.MODID}.healing_machine.tooltip2".asTranslated().gray())
    }

}