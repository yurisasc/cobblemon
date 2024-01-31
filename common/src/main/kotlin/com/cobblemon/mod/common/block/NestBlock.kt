/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.block.entity.NestBlockEntity
import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.util.DataKeys
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.Waterloggable
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.Fluid
import net.minecraft.fluid.FluidState
import net.minecraft.fluid.Fluids
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.StringIdentifiable
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World

class NestBlock(val variant: NestVariant, settings: Settings) : BlockWithEntity(settings), Waterloggable {

    init {
        defaultState = stateManager.defaultState
            .with(Properties.WATERLOGGED, false)
            .with(HAS_EGG, false)
    }

    enum class NestVariant(val id: String) : StringIdentifiable {
        CAVE("cave_nest"),
        NETHER("nether_nest"),
        WATER("water_nest"),
        BASIC("basic_nest");

        override fun asString(): String {
            return id
        }
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>?) {
        builder?.add(HAS_EGG)
        builder?.add(Properties.WATERLOGGED)
    }

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        super.onUse(state, world, pos, player, hand, hit)

        val entity = world.getBlockEntity(pos) as? NestBlockEntity
        entity?.onUse()
        return ActionResult.SUCCESS
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return NestBlockEntity(pos, state)
    }

    override fun getRenderType(state: BlockState?) = BlockRenderType.MODEL

    override fun canFillWithFluid(
        world: BlockView?,
        pos: BlockPos?,
        state: BlockState?,
        fluid: Fluid?
    ): Boolean {
        return variant == NestVariant.WATER && super.canFillWithFluid(world, pos, state, fluid)
    }

    override fun getFluidState(state: BlockState): FluidState? {
        return if (state.get(PCBlock.WATERLOGGED)) {
            Fluids.WATER.getStill(false)
        } else super.getFluidState(state)
    }

    override fun getPlacementState(ctx: ItemPlacementContext?): BlockState? {
        if (variant == NestVariant.WATER) {
            return defaultState.with(Properties.WATERLOGGED, ctx?.world?.getFluidState(ctx.blockPos)?.fluid == Fluids.WATER)
        }
        return defaultState
    }

    companion object {
        val HAS_EGG = BooleanProperty.of("has_egg")
    }
}