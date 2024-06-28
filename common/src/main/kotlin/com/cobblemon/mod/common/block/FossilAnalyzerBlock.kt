/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.api.multiblock.MultiblockBlock
import com.cobblemon.mod.common.block.entity.FossilAnalyzerBlockEntity
import com.cobblemon.mod.common.block.entity.FossilMultiblockEntity
import com.cobblemon.mod.common.block.multiblock.FossilMultiblockStructure
import com.cobblemon.mod.common.block.multiblock.FossilMultiblockBuilder
import com.mojang.serialization.MapCodec
import net.minecraft.world.level.block.Block
import net.minecraft.inventory.SidedInventory
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.block.InventoryProvider
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.core.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.WorldAccess
import net.minecraft.world.level.block.BaseEntityBlock

class FossilAnalyzerBlock(properties: Settings) : MultiblockBlock(properties), InventoryProvider {
    init {
        defaultState = defaultState
            .with(HorizontalFacingBlock.FACING, Direction.NORTH)
            .with(ON, false)
    }

    override fun createMultiBlockEntity(pos: BlockPos, state: BlockState): FossilMultiblockEntity {
        return FossilAnalyzerBlockEntity(pos, state, FossilMultiblockBuilder(pos))
    }

    override fun getCodec(): MapCodec<out BaseEntityBlock> {
        return CODEC
    }

    override fun <T : BlockEntity?> getTicker(
        world: Level?,
        state: BlockState?,
        type: BlockEntityType<T>?
    ): BlockEntityTicker<T>? = validateTicker(type, CobblemonBlockEntities.FOSSIL_ANALYZER, FossilMultiblockStructure.TICKER::tick)

    override fun getPlacementState(blockPlaceContext: ItemPlacementContext): BlockState? {
        return defaultState.with(HorizontalFacingBlock.FACING, blockPlaceContext.horizontalPlayerFacing)
    }

    override fun getInventory(
        state: BlockState,
        world: WorldAccess,
        pos: BlockPos
    ): SidedInventory {
        val analyzerEntity = world.getBlockEntity(pos) as FossilAnalyzerBlockEntity

        return analyzerEntity.inv
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(HorizontalFacingBlock.FACING)
        builder.add(ON)
    }

    companion object {
        val ON = BooleanProperty.of("on")

        val CODEC = createCodec(::FossilAnalyzerBlock)
    }
}