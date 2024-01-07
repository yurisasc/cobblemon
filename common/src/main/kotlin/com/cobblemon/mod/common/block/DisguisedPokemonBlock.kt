/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.block.Block
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.sound.SoundCategory
import net.minecraft.state.StateManager
import net.minecraft.util.ActionResult
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class DisguisedPokemonBlock(
    settings: Settings,
    //Should probably be a [PokemonProperties] or maybe a PokemonProperties.parse string
    val pokemonArgs: String,
    val levelRange: IntRange = 1..1
) : Block(settings) {

    init {
        defaultState = this.stateManager.defaultState
            .with(HorizontalFacingBlock.FACING, Direction.NORTH)
    }

    private val facingToYaw: HashMap<Direction, Float> = hashMapOf(
        Direction.NORTH to -179.0F,
        Direction.WEST to 90.0F,
        Direction.SOUTH to 0.0F,
        Direction.EAST to -90.0F
    )

    private fun spawnPokemon(world: World, pos: BlockPos, state: BlockState) : ActionResult {
        val properties = pokemonArgs + " level=${levelRange.random()}"
        val pokemon = PokemonProperties.parse(properties)
        val entity = pokemon.createEntity(world)
        entity.refreshPositionAndAngles(pos, entity.yaw, entity.pitch)
        entity.dataTracker.set(PokemonEntity.SPAWN_DIRECTION, facingToYaw[state[HorizontalFacingBlock.FACING]])
        world.spawnEntity(entity)
        world.playSound(null, pos, CobblemonSounds.GIMMIGHOUL_REVEAL, SoundCategory.BLOCKS)

        world.removeBlock(pos, false)
        return ActionResult.SUCCESS
    }

    @Deprecated("Deprecated in Java")
    override fun getRenderType(state: BlockState?): BlockRenderType {
        return BlockRenderType.MODEL
    }

    @Deprecated("Deprecated in Java")
    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        return spawnPokemon(world, pos, state)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(HorizontalFacingBlock.FACING)
    }

    override fun rotate(blockState: BlockState, rotation: BlockRotation): BlockState {
        return blockState.with(HorizontalFacingBlock.FACING, rotation.rotate(blockState.get(HorizontalFacingBlock.FACING)))
    }

    override fun mirror(blockState: BlockState, mirror: BlockMirror): BlockState {
        return blockState.rotate(mirror.getRotation(blockState.get(HorizontalFacingBlock.FACING)))
    }

    override fun getPlacementState(blockPlaceContext: ItemPlacementContext): BlockState {
        return this.defaultState.with(HorizontalFacingBlock.FACING, blockPlaceContext.horizontalPlayerFacing.opposite)
    }

}