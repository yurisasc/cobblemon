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
import com.cobblemon.mod.common.api.pasture.PastureLink
import com.cobblemon.mod.common.api.pasture.PastureLinkManager
import com.cobblemon.mod.common.block.entity.PokemonPastureBlockEntity
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.net.messages.client.pasture.OpenPasturePacket
import com.cobblemon.mod.common.util.isInBattle
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

class PastureBlock(properties: Settings): BlockWithEntity(properties) {
    companion object {
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState) = PokemonPastureBlockEntity(pos, state)

    init {
        defaultState = this.stateManager.defaultState.with(HorizontalFacingBlock.FACING, Direction.NORTH)
//            .with(PCBlock.ON, false)
    }

    override fun getRenderType(state: BlockState) = BlockRenderType.MODEL
    override fun getPlacementState(blockPlaceContext: ItemPlacementContext) = defaultState.with(HorizontalFacingBlock.FACING, blockPlaceContext.playerFacing)

    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun canPathfindThrough(blockState: BlockState, blockGetter: BlockView, blockPos: BlockPos, pathComputationType: NavigationType) = false
    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(HorizontalFacingBlock.FACING)
    }

    override fun onBroken(world: WorldAccess, pos: BlockPos, state: BlockState) {
        val blockEntity = world.getBlockEntity(pos) as? PokemonPastureBlockEntity ?: return
        super.onBroken(world, pos, state)
        blockEntity.releaseAllPokemon()
    }

    override fun <T : BlockEntity?> getTicker(world: World, state: BlockState, type: BlockEntityType<T>): BlockEntityTicker<T>? {
        return checkType(type, CobblemonBlockEntities.PASTURE_BLOCK, PokemonPastureBlockEntity.TICKER::tick)
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
            val blockEntity = world.getBlockEntity(pos) as? PokemonPastureBlockEntity ?: return ActionResult.FAIL
            val pcId = Cobblemon.storage.getPC(player.uuid).uuid

            CobblemonNetwork.sendPacketToPlayer(
                player = player,
                packet = OpenPasturePacket(
                    pcId = pcId,
                    pasturePos = pos,
                    totalTethered = blockEntity.tetheredPokemon.size,
                    tetheredPokemon = blockEntity.tetheredPokemon.filter { it.playerId == player.uuid }.mapNotNull {
                        val pokemon = it.getPokemon() ?: return@mapNotNull null
                        OpenPasturePacket.PasturePokemonDataDTO(
                            pokemonId = it.pokemonId,
                            name = pokemon.displayName,
                            species = pokemon.species.resourceIdentifier,
                            aspects = pokemon.aspects,
                            entityKnown = (player.world.getEntityById(it.entityId) as? PokemonEntity)?.tethering?.tetheringId == it.tetheringId
                        )
                    }
                )
            )

            PastureLinkManager.createLink(player.uuid, PastureLink(UUID.randomUUID(), pcId, world.dimensionKey.value, pos))
            return ActionResult.SUCCESS
        }

        return ActionResult.SUCCESS
    }
}