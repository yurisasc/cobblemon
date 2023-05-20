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
import com.cobblemon.mod.common.block.entity.PokemonPastureBlockEntity
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.net.messages.client.pasture.OpenPasturePacket
import com.cobblemon.mod.common.util.isInBattle
import com.cobblemon.mod.common.util.playSoundServer
import com.cobblemon.mod.common.util.toVec3d
import com.cobblemon.mod.common.util.voxelShape
import net.minecraft.block.Block
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.state.StateManager
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import net.minecraft.world.WorldView
import java.util.*
import net.minecraft.block.Blocks
import net.minecraft.block.ShapeContext
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.EnumProperty
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.StringIdentifiable
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes

class PastureBlock(properties: Settings): BlockWithEntity(properties) {
    companion object {
        val PART = EnumProperty.of("part", PasturePart::class.java)
        val ON = BooleanProperty.of("on")

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
                    voxelShape(0.25, 0.75, 0.3125, 0.75, 1.0, 0.8125, direction)
                )
            }
        }
    }

    enum class PasturePart(private val label: String) : StringIdentifiable {
        TOP("top"),
        BOTTOM("bottom");
        override fun asString() = label
    }

    override fun createBlockEntity(blockPos: BlockPos, blockState: BlockState) = if (blockState.get(PART) == PasturePart.BOTTOM) PokemonPastureBlockEntity(blockPos, blockState) else null


    init {
        defaultState = this.stateManager.defaultState.with(HorizontalFacingBlock.FACING, Direction.NORTH)
            .with(PART, PasturePart.BOTTOM)
            .with(ON, false)
    }

    override fun getRenderType(state: BlockState) = BlockRenderType.MODEL
    override fun getPlacementState(blockPlaceContext: ItemPlacementContext): BlockState? {
        val abovePosition = blockPlaceContext.blockPos.up()
        val world = blockPlaceContext.world
        if (world.getBlockState(abovePosition).canReplace(blockPlaceContext) && !world.isOutOfHeightLimit(abovePosition)) {
            return defaultState
                .with(HorizontalFacingBlock.FACING, blockPlaceContext.horizontalPlayerFacing)
                .with(PART, PasturePart.BOTTOM)
        }

        return null
    }

    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        return true
    }

    fun getPositionOfOtherPart(state: BlockState, pos: BlockPos): BlockPos {
        return if (state.get(PART) == PasturePart.BOTTOM) {
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

    private fun isBase(state: BlockState): Boolean = state.get(PART) == PasturePart.BOTTOM

    @Deprecated("Deprecated in Java")
    override fun canPathfindThrough(blockState: BlockState, blockGetter: BlockView, blockPos: BlockPos, pathComputationType: NavigationType) = false
    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(HorizontalFacingBlock.FACING)
        builder.add(PART)
        builder.add(ON)
    }

    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity?) {
        super.onBreak(world, pos, state, player)
        val otherPart = world.getBlockState(getPositionOfOtherPart(state, pos))
        if (otherPart.block is PastureBlock) {
            world.setBlockState(getPositionOfOtherPart(state, pos), Blocks.AIR.defaultState, 35)
            world.syncWorldEvent(player, 2001, getPositionOfOtherPart(state, pos), Block.getRawIdFromState(otherPart))
        }
    }

    override fun onBroken(world: WorldAccess, pos: BlockPos, state: BlockState) {
        val blockEntity = world.getBlockEntity(pos) as? PokemonPastureBlockEntity ?: return
        super.onBroken(world, pos, state)
        blockEntity.onBroken()
    }

    override fun <T : BlockEntity?> getTicker(world: World, state: BlockState, type: BlockEntityType<T>): BlockEntityTicker<T>? {
        return checkType(type, CobblemonBlockEntities.PASTURE, PokemonPastureBlockEntity.TICKER::tick)
    }

    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack?) {
        world.setBlockState(pos.up(), state.with(PART, PasturePart.TOP) as BlockState, 3)
        world.updateNeighbors(pos, Blocks.AIR)
        state.updateNeighbors(world, pos, 3)
    }

    @Deprecated("Deprecated in Java")
    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        if (player is ServerPlayerEntity && !player.isInBattle()) {
            val basePos = getBasePosition(state, pos)

            // Remove any duplicate block entities that may exist
            world.getBlockEntity(basePos.up())?.markRemoved()

            val baseEntity = world.getBlockEntity(basePos)
            if (baseEntity !is PokemonPastureBlockEntity) return ActionResult.SUCCESS


            val pcId = Cobblemon.storage.getPC(player.uuid).uuid

            CobblemonNetwork.sendPacketToPlayer(
                player = player,
                packet = OpenPasturePacket(
                    pcId = pcId,
                    pasturePos = pos,
                    totalTethered = baseEntity.tetheredPokemon.size,
                    tetheredPokemon = baseEntity.tetheredPokemon.filter { it.playerId == player.uuid }.mapNotNull {
                        val pokemon = it.getPokemon() ?: return@mapNotNull null
                        OpenPasturePacket.PasturePokemonDataDTO(
                            pokemonId = it.pokemonId,
                            name = pokemon.getDisplayName(),
                            species = pokemon.species.resourceIdentifier,
                            aspects = pokemon.aspects,
                            entityKnown = (player.world.getEntityById(it.entityId) as? PokemonEntity)?.tethering?.tetheringId == it.tetheringId
                        )
                    }
                )
            )

            PastureLinkManager.createLink(player.uuid, PastureLink(UUID.randomUUID(), pcId, world.dimensionKey.value, pos))

            world.playSoundServer(
                position = pos.toVec3d(),
                sound = CobblemonSounds.PC_ON,
                volume = 1F,
                pitch = 1F
            )

            return ActionResult.SUCCESS
        }

        return ActionResult.SUCCESS
    }

    @Deprecated("Deprecated in Java")
    override fun getOutlineShape(blockState: BlockState, blockGetter: BlockView, blockPos: BlockPos, collisionContext: ShapeContext): VoxelShape {
        return if (blockState.get(PART) == PasturePart.TOP)  {
            when (blockState.get(HorizontalFacingBlock.FACING)) {
                Direction.SOUTH -> SOUTH_AABB_TOP
                Direction.WEST -> WEST_AABB_TOP
                Direction.EAST -> EAST_AABB_TOP
                else -> NORTH_AABB_TOP
            }
        } else {
            when (blockState.get(HorizontalFacingBlock.FACING)) {
                Direction.SOUTH -> SOUTH_AABB_BOTTOM
                Direction.WEST -> WEST_AABB_BOTTOM
                Direction.EAST -> EAST_AABB_BOTTOM
                else -> NORTH_AABB_BOTTOM
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun rotate(blockState: BlockState, rotation: BlockRotation) =
        blockState.with(HorizontalFacingBlock.FACING, rotation.rotate(blockState.get(HorizontalFacingBlock.FACING)))

    @Deprecated("Deprecated in Java")
    override fun mirror(blockState: BlockState, mirror: BlockMirror): BlockState {
        return blockState.rotate(mirror.getRotation(blockState.get(HorizontalFacingBlock.FACING)))
    }

    @Deprecated("Deprecated in Java")
    override fun onStateReplaced(state: BlockState, world: World, pos: BlockPos?, newState: BlockState, moved: Boolean) {
        if (!state.isOf(newState.block)) super.onStateReplaced(state, world, pos, newState, moved)
    }
}