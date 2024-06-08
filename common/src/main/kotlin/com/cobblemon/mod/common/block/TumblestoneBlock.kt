/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.api.tags.CobblemonBlockTags
import com.cobblemon.mod.common.block.chest.GildedChestBlock
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.block.Block
import net.minecraft.block.FacingBlock
import net.minecraft.block.*
import net.minecraft.fluid.FluidState
import net.minecraft.fluid.Fluids
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties.WATERLOGGED
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView
import net.minecraft.world.WorldAccess

@Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
class TumblestoneBlock(
    settings: Settings,
    stage: Int,
    height: Int,
    xzOffset: Int,
    nextStage: Block?
) : GrowableStoneBlock(settings, stage, height, xzOffset, nextStage), Waterloggable {

    // TODO(Deltric): Look into Block.CODEC for being optional more
    companion object {
        val CODEC: MapCodec<TumblestoneBlock> = RecordCodecBuilder.mapCodec { it.group(
            createSettingsCodec(),
            PrimitiveCodec.INT.fieldOf("stage").forGetter { it.stage },
            PrimitiveCodec.INT.fieldOf("height").forGetter { it.height },
            PrimitiveCodec.INT.fieldOf("xzOffset").forGetter { it.xzOffset },
            Block.CODEC.fieldOf("nextStage").forGetter { it.nextStage }
        ).apply(it, ::TumblestoneBlock) }
    }

    init {
        this.defaultState = this.stateManager.defaultState
            .with(FACING, Direction.DOWN)
            .with(WATERLOGGED, false)
    }

    override fun canGrow(pos: BlockPos, world: BlockView): Boolean {
        if (stage == MAX_STAGE) return false
        val iterator: Iterator<BlockPos> =
            BlockPos.iterate(pos.add(-1, -1, -1), pos.add(1, 1, 1))
                .iterator()

        var blockPos: BlockPos
        do { if (!iterator.hasNext()) { return false }
            blockPos = iterator.next()
        } while (!world.getBlockState(blockPos).isIn(CobblemonBlockTags.TUMBLESTONE_HEAT_SOURCE))

        return true
    }

    override fun getCodec(): MapCodec<out FacingBlock> {
        return CODEC
    }

    override fun getFluidState(state: BlockState): FluidState {
        return if (state.get(WATERLOGGED)) {
            Fluids.WATER.getStill(false)
        } else super.getFluidState(state)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(WATERLOGGED)
    }

    override fun getPlacementState(blockPlaceContext: ItemPlacementContext): BlockState? {
        return super.getPlacementState(blockPlaceContext)?.with(WATERLOGGED, blockPlaceContext.world.getFluidState(blockPlaceContext.blockPos).fluid == Fluids.WATER)

    }

    override fun getStateForNeighborUpdate(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        world: WorldAccess,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState? {
        if (state.get(WATERLOGGED)) world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world))
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos)
    }
}