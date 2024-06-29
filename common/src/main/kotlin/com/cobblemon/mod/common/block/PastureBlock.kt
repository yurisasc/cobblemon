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
import com.cobblemon.mod.common.CobblemonNetwork
import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.pasture.PastureLink
import com.cobblemon.mod.common.api.pasture.PastureLinkManager
import com.cobblemon.mod.common.api.pasture.PasturePermissionControllers
import com.cobblemon.mod.common.block.entity.PokemonPastureBlockEntity
import com.cobblemon.mod.common.net.messages.client.pasture.OpenPasturePacket
import com.cobblemon.mod.common.util.isInBattle
import com.cobblemon.mod.common.util.playSoundServer
import com.cobblemon.mod.common.util.toVec3d
import com.cobblemon.mod.common.util.voxelShape
import com.mojang.serialization.MapCodec
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.fluid.FluidState
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.Mirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.StringRepresentable
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.InteractionResult
import net.minecraft.world.WorldAccess
import net.minecraft.world.WorldEvents
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import java.util.*

@Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
class PastureBlock(settings: Properties): BaseEntityBlock(properties), SimpleWaterloggedBlock, PreEmptsExplosion {
    companion object {
        val CODEC = createCodec(::PastureBlock)

        val PART: EnumProperty<PasturePart> = EnumProperty.create("part", PasturePart::class.java)
        val ON: BooleanProperty = BooleanProperty.create("on")
        val WATERLOGGED: BooleanProperty = BooleanProperty.create("waterlogged")

        private val SOUTH_AABB_TOP = buildCollider(top = true, Direction.NORTH)
        private val NORTH_AABB_TOP = buildCollider(top = true, Direction.SOUTH)
        private val WEST_AABB_TOP = buildCollider(top = true, Direction.WEST)
        private val EAST_AABB_TOP = buildCollider(top = true, Direction.EAST)

        private val SOUTH_AABB_BOTTOM = buildCollider(top = false, Direction.SOUTH)
        private val NORTH_AABB_BOTTOM = buildCollider(top = false, Direction.NORTH)
        private val WEST_AABB_BOTTOM = buildCollider(top = false, Direction.WEST)
        private val EAST_AABB_BOTTOM = buildCollider(top = false, Direction.EAST)

        private fun buildCollider(top: Boolean, direction: Direction): VoxelShape {
            if (top) {
                return VoxelShapes.union(
                    voxelShape(0.1875, 0.0, 0.0625, 0.8125, 0.0625, 0.3125, direction),
                    voxelShape(0.125, 0.0, 0.375, 0.875, 0.1875, 0.9375, direction),
                    voxelShape(0.1875, 0.1875, 0.4375, 0.8125, 0.6875, 0.9375, direction),
                    voxelShape(0.8125, 0.1875, 0.375, 0.875, 0.6875, 0.9375, direction),
                    voxelShape(0.125, 0.1875, 0.375, 0.1875, 0.6875, 0.9375, direction),
                    voxelShape(0.125, 0.6875, 0.375, 0.875, 0.75, 0.9375, direction)
                )
            } else {
                return VoxelShapes.union(
                    voxelShape(0.875, 0.0, 0.0, 1.0, 1.0, 0.125, direction),
                    voxelShape(0.125, 0.0, 0.0, 0.875, 0.125, 0.125, direction),
                    voxelShape(0.125, 0.875, 0.0, 0.875, 1.0, 0.125, direction),
                    voxelShape(0.125, 0.125, 0.0625, 0.875, 0.875, 0.125, direction),
                    voxelShape(0.0625, 0.125, 0.125, 0.125, 0.875, 0.875, direction),
                    voxelShape(0.0, 0.0, 0.0, 0.125, 1.0, 0.125, direction),
                    voxelShape(0.125, 0.0, 0.125, 0.875, 0.125, 1.0, direction),
                    voxelShape(0.875, 0.125, 0.125, 0.9375, 0.875, 0.875, direction),
                    voxelShape(0.125, 0.875, 0.125, 0.875, 1.0, 1.0, direction),
                    voxelShape(0.875, 0.0, 0.875, 1.0, 1.0, 1.0, direction),
                    voxelShape(0.875, 0.0, 0.125, 1.0, 0.125, 0.875, direction),
                    voxelShape(0.875, 0.875, 0.125, 1.0, 1.0, 0.875, direction),
                    voxelShape(0.0, 0.875, 0.125, 0.125, 1.0, 0.875, direction),
                    voxelShape(0.0, 0.0, 0.125, 0.125, 0.125, 0.875, direction),
                    voxelShape(0.0, 0.0, 0.875, 0.125, 1.0, 1.0, direction),
                    voxelShape(0.0, 0.125, 0.375, 0.0625, 0.875, 0.625, direction),
                    voxelShape(0.9375, 0.125, 0.375, 1.0, 0.875, 0.625, direction),
                    voxelShape(0.1875, 0.1875, 0.05625, 0.8125, 0.75, 0.05625, direction),
                    voxelShape(0.1875, 0.125, 0.3125, 0.8125, 0.3125, 0.875, direction),
                    voxelShape(0.1875, 0.125, 0.3125, 0.8125, 0.3125, 0.875, direction),
                    voxelShape(0.1875, 0.0625, 0.875, 0.8125, 0.25, 0.875, direction),
                    voxelShape(0.1875, 0.25, 0.25, 0.1875, 0.4375, 0.875, direction),
                    voxelShape(0.8125, 0.25, 0.25, 0.8125, 0.4375, 0.875, direction),
                    voxelShape(0.1875, 0.3125, 0.3125, 0.8125, 0.5, 0.3125, direction),
                    voxelShape(0.25, 0.75, 0.3125, 0.75, 1.0, 0.8125, direction),
                    // I'm lazy and this gets optimized in union anyway
                    voxelShape(0.0, 0.0, 0.0, 0.0625, 1.0, 1.0, direction),
                    voxelShape(0.9375, 0.0, 0.0, 1.0, 1.0, 1.0, direction),
                )
            }
        }
    }

    enum class PasturePart(private val label: String) : StringRepresentable {
        TOP("top"),
        BOTTOM("bottom");
        override fun getSerializedName(): String = label
    }

    override fun createBlockEntity(blockPos: BlockPos, blockState: BlockState) = if (blockState.getValue(PART) == PasturePart.BOTTOM) PokemonPastureBlockEntity(blockPos, blockState) else null


    init {
        registerDefaultState(stateDefinition.any()
            .setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH)
            .setValue(PART, PasturePart.BOTTOM)
            .setValue(ON, false))
    }

    override fun getRenderShape(state: BlockState) = RenderShape.MODEL
    override fun getStateForPlacement(blockPlaceContext: BlockPlaceContext): BlockState? {
        val abovePosition = blockPlaceContext.clickedPos.above()
        val world = blockPlaceContext.level
        if (world.getBlockState(abovePosition).canReplace(blockPlaceContext) && !world.isOutOfHeightLimit(abovePosition)) {
            return defaultBlockState()
                .setValue(HorizontalDirectionalBlock.FACING, blockPlaceContext.horizontalDirection)
                .setValue(PART, PasturePart.BOTTOM)
                .setValue(WATERLOGGED, blockPlaceContext.level.getFluidState(blockPlaceContext.clickedPos).type == Fluids.WATER)
        }

        return null
    }

    override fun canSurvive(state: BlockState, world: LevelReader, pos: BlockPos): Boolean {
        return true
    }

    fun getPositionOfOtherPart(state: BlockState, pos: BlockPos): BlockPos {
        return if (state.contains(PART) && state.get(PART) == PasturePart.BOTTOM) {
            pos.up()
        } else {
            pos.down()
        }
    }

    fun getBasePosition(state: BlockState, pos: BlockPos): BlockPos {
        return if (isBase(state)) {
            pos
        } else {
            pos.down()
        }
    }

    private fun isBase(state: BlockState): Boolean = state.contains(PART) && state.get(PART) == PasturePart.BOTTOM

    override fun codec(): MapCodec<out BaseEntityBlock> {
        return CODEC
    }

    override fun isPathfindable(
        blockState: BlockState,
        pathComputationType: PathComputationType
    ): Boolean = false

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(HorizontalDirectionalBlock.FACING)
        builder.add(PART)
        builder.add(ON)
        builder.add(WATERLOGGED)
    }

    fun checkBreakEntity(world: WorldAccess, state: BlockState, pos: BlockPos) {
        if (state.get(PART) == PasturePart.TOP) {
            return
        }
        val blockEntity = world.getBlockEntity(pos)
        if (blockEntity is PokemonPastureBlockEntity) {
            blockEntity.onBroken()
        }
    }

    override fun onBreak(world: Level, pos: BlockPos, state: BlockState, player: Player?): BlockState {
        checkBreakEntity(world, state, pos)
        if (!world.isClient && player?.isCreative == true) {
            var blockPos: BlockPos = BlockPos.ORIGIN
            var blockState: BlockState = state
            val part = state.get(PART)
            if (part == PasturePart.TOP && world.getBlockState(pos.down().also { blockPos = it }).also { blockState = it }.isOf(state.block) && blockState.getValue(PART) == PasturePart.BOTTOM) {
                checkBreakEntity(world, blockState, blockPos)
                val blockState2 = if (blockState.fluidState.isOf(Fluids.WATER)) Blocks.WATER.defaultState else Blocks.AIR.defaultState
                world.setBlockState(blockPos, blockState2, NOTIFY_ALL or SKIP_DROPS)
                world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, blockPos, getRawIdFromState(blockState))
            }
        }
        return super.onBreak(world, pos, state, player)
    }

    override fun whenExploded(world: Level, state: BlockState, pos: BlockPos) {
        val blockEntity = world.getBlockEntity(pos) as? PokemonPastureBlockEntity ?: return
        blockEntity.onBroken()
    }

    override fun <T : BlockEntity?> getTicker(world: Level, state: BlockState, type: BlockEntityType<T>): BlockEntityTicker<T>? {
        return validateTicker(type, CobblemonBlockEntities.PASTURE, PokemonPastureBlockEntity.TICKER::tick)
    }

    override fun onPlaced(world: Level, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack?) {
        world.setBlockState(
            pos.up(),
            state.with(PART, PasturePart.TOP).with(WATERLOGGED, world.getFluidState((pos.up())).fluid == Fluids.WATER) as BlockState,
            3
        )
        world.updateNeighbors(pos, Blocks.AIR)
        state.updateNeighbors(world, pos, 3)

        if (world is ServerLevel && placer is ServerPlayer) {
            val blockEntity = world.getBlockEntity(pos) as? PokemonPastureBlockEntity ?: return
            blockEntity.ownerId = placer.uuid
            blockEntity.ownerName = placer.gameProfile.name
            blockEntity.markDirty()
        }
    }

    override fun useWithoutItem(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hit: BlockHitResult
    ): InteractionResult {
        if (player is ServerPlayer && !player.isInBattle()) {
            val basePos = getBasePosition(state, pos)

            // Remove any duplicate block entities that may exist
            world.getBlockEntity(basePos.above())?.setRemoved()

            val baseEntity = world.getBlockEntity(basePos)
            if (baseEntity !is PokemonPastureBlockEntity) return InteractionResult.SUCCESS


            val pcId = Cobblemon.storage.getPC(player.uuid).uuid
            val linkId = UUID.randomUUID()

            val perms = PasturePermissionControllers.permit(player, baseEntity)
            CobblemonNetwork.sendPacketToPlayer(
                player = player,
                packet = OpenPasturePacket(
                    pcId = pcId,
                    pastureId = linkId,
                    permissions = perms,
                    limit = baseEntity.getMaxTethered(),
                    tetheredPokemon = baseEntity.tetheredPokemon.mapNotNull { it.toDTO(player) }
                )
            )

            PastureLinkManager.createLink(player.uuid, PastureLink(linkId, pcId, ResourceLocation.tryParse(world.dimensionEntry.idAsString)!!, getBasePosition(state, pos), perms))

            world.playSoundServer(
                position = pos.toVec3d(),
                sound = CobblemonSounds.PC_ON,
                volume = 0.5F,
                pitch = 1F
            )

            return InteractionResult.SUCCESS
        }

        return InteractionResult.SUCCESS
    }

    override fun getShape(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        collisionContext: CollisionContext
    ): VoxelShape {
        return if (blockState.getValue(PART) == PasturePart.TOP)  {
            when (blockState.getValue(HorizontalDirectionalBlock.FACING)) {
                Direction.SOUTH -> SOUTH_AABB_TOP
                Direction.WEST -> WEST_AABB_TOP
                Direction.EAST -> EAST_AABB_TOP
                else -> NORTH_AABB_TOP
            }
        } else {
            when (blockState.getValue(HorizontalDirectionalBlock.FACING)) {
                Direction.SOUTH -> SOUTH_AABB_BOTTOM
                Direction.WEST -> WEST_AABB_BOTTOM
                Direction.EAST -> EAST_AABB_BOTTOM
                else -> NORTH_AABB_BOTTOM
            }
        }
    }

    override fun rotate(blockState: BlockState, rotation: BlockRotation): BlockState = blockState.with(
        HorizontalDirectionalBlock.FACING, rotation.rotate(blockState.getValue(
            HorizontalDirectionalBlock.FACING)))

    override fun mirror(blockState: BlockState, mirror: Mirror): BlockState {
        return blockState.rotate(mirror.getRotation(blockState.getValue(HorizontalDirectionalBlock.FACING)))
    }

    override fun onStateReplaced(state: BlockState, world: Level, pos: BlockPos?, newState: BlockState, moved: Boolean) {
        if (!state.isOf(newState.block)) super.onStateReplaced(state, world, pos, newState, moved)
    }

    override fun getFluidState(state: BlockState): FluidState? {
        return if (state.get(WATERLOGGED)) {
            Fluids.WATER.getStill(false)
        } else super.getFluidState(state)
    }

    override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        world: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world))
        }

        val isPasture = neighborState.`is`(this)
        val part = state.getValue(PART)
        if (!isPasture && part == PasturePart.TOP && neighborPos == pos.below()) {
            return Blocks.AIR.defaultBlockState()
        } else if (!isPasture && part == PasturePart.BOTTOM && neighborPos == pos.above()) {
            checkBreakEntity(world, state, pos)
            return Blocks.AIR.defaultBlockState()
        }

        return state
    }
}