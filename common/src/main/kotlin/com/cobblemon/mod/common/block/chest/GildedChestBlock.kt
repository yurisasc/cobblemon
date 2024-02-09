/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block.chest

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.block.entity.GildedChestBlockEntity
import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.block.Block
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.Blocks
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.block.ShapeContext
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.entity.mob.PiglinBrain
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.sound.SoundCategory
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.ItemScatterer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World

class GildedChestBlock(settings: Settings, val type: Type = Type.RED) : BlockWithEntity(settings) {

    init {
        defaultState = defaultState.with(Properties.HORIZONTAL_FACING, Direction.SOUTH)
    }

    companion object {
        val POKEMON_ARGS = "gimmighoul"
        val LEVEL_RANGE = 5..30

        val SOUTH_OUTLINE = VoxelShapes.union(
            VoxelShapes.cuboid(0.0, 0.0, 0.25, 1.0, 1.0, 0.9375)
        )
        val NORTH_OUTLINE = VoxelShapes.union(
            VoxelShapes.cuboid(0.0, 0.0, 0.0625, 1.0, 1.0, 0.75)
        )
        val WEST_OUTLINE = VoxelShapes.union(
            VoxelShapes.cuboid(0.0625, 0.0, 0.0, 0.75, 1.0, 1.0)
        )
        val EAST_OUTLINE = VoxelShapes.union(
            VoxelShapes.cuboid(0.25, 0.0, 0.0, 0.9375, 1.0, 1.0)
        )
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState) = GildedChestBlockEntity(pos, state, type)

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        return when (state.get(HorizontalFacingBlock.FACING)) {
            Direction.NORTH -> NORTH_OUTLINE
            Direction.SOUTH -> SOUTH_OUTLINE
            Direction.WEST -> WEST_OUTLINE
            else -> EAST_OUTLINE
        }
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(Properties.HORIZONTAL_FACING)
    }

    private val facingToYaw: HashMap<Direction, Float> = hashMapOf(
        Direction.NORTH to -179.0F,
        Direction.WEST to 90.0F,
        Direction.SOUTH to 0.0F,
        Direction.EAST to -90.0F
    )

    override fun getName(): MutableText {
        return if (isFake()) Text.translatable("block.cobblemon.gilded_chest") else super.getName()
    }

    fun isFake() = (type == Type.FAKE)

    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity) {
        if (isFake()) {
            spawnPokemon(world, pos, state, player)
            world.setBlockState(pos, Blocks.AIR.defaultState)
        } else super.onBreak(world, pos, state, player)
    }

    private fun spawnPokemon(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity) : ActionResult {
        val properties = "$POKEMON_ARGS lvl=${LEVEL_RANGE.random()}"
        val pokemon = PokemonProperties.parse(properties)
        val entity = pokemon.createEntity(world)

        // The yaw based on the block's facing direction
        val yaw = facingToYaw[state[HorizontalFacingBlock.FACING]] ?: 0.0F

        entity.dataTracker.set(PokemonEntity.SPAWN_DIRECTION, facingToYaw[state[HorizontalFacingBlock.FACING]])
        entity.refreshPositionAndAngles(pos, yaw, entity.pitch)
        world.spawnEntity(entity)
        world.playSound(null, pos, CobblemonSounds.GIMMIGHOUL_REVEAL, SoundCategory.NEUTRAL)

        world.removeBlock(pos, false)
        if (!CobblemonClient.storage.myParty.isEmpty() && !player.isCreative) entity.forceBattle(player)
        return ActionResult.SUCCESS
    }

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        if (isFake()) return spawnPokemon(world, pos, state, player)
        val entity = world.getBlockEntity(pos) as? GildedChestBlockEntity ?: return ActionResult.FAIL
        player.openHandledScreen(entity)
        if (!player.world.isClient) {
            PiglinBrain.onGuardedBlockInteracted(player, true)
        }
        val state = entity.poseableState
        state.currentModel?.let {
            it.moveToPose(null, state, it.getPose("OPEN")!!)
        }
        return ActionResult.SUCCESS
    }

    override fun onStateReplaced(
        state: BlockState,
        world: World,
        pos: BlockPos,
        newState: BlockState,
        moved: Boolean
    ) {
        if (!state.isOf(newState.block)) {
            val chest = world.getBlockEntity(pos) as GildedChestBlockEntity
            ItemScatterer.spawn(world, pos, chest.inventoryContents)
        }
    }

    override fun getRenderType(state: BlockState?) = BlockRenderType.ENTITYBLOCK_ANIMATED

    override fun getPlacementState(blockPlaceContext: ItemPlacementContext): BlockState? {
//        val abovePosition = blockPlaceContext.blockPos.up()
//        val world = blockPlaceContext.world
//        if (world.getBlockState(abovePosition).canReplace(blockPlaceContext) && !world.isOutOfHeightLimit(abovePosition)) {
            return defaultState.with(HorizontalFacingBlock.FACING, blockPlaceContext.horizontalPlayerFacing.opposite)
//        }
        // return null
    }

    @Deprecated("Deprecated in Java")
    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState {
        return state.with(
            Properties.HORIZONTAL_FACING, rotation.rotate(
                state.get(Properties.HORIZONTAL_FACING) as Direction
            )
        ) as BlockState
    }

    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState {
        return state.rotate(mirror.getRotation(state.get(Properties.HORIZONTAL_FACING) as Direction))
    }

    enum class Type(val poserId: Identifier) {
        RED(cobblemonResource("gilded_chest")),
        BLUE(cobblemonResource("blue_gilded_chest")),
        GREEN(cobblemonResource("green_gilded_chest")),
        PINK(cobblemonResource("pink_gilded_chest")),
        WHITE(cobblemonResource("white_gilded_chest")),
        BLACK(cobblemonResource("black_gilded_chest")),
        YELLOW(cobblemonResource("yellow_gilded_chest")),
        FAKE(cobblemonResource("gilded_chest"))
    }

    @Deprecated("Deprecated in Java")
    override fun canPathfindThrough(state: BlockState?, world: BlockView?, pos: BlockPos?, type: NavigationType?): Boolean {
        return false
    }

}