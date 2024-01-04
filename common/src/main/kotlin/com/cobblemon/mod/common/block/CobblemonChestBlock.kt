/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.block.entity.CobblemonChestBlockEntity
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.ChestBlock
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.entity.ChestBlockEntity
import net.minecraft.util.math.BlockPos
import java.util.function.Supplier

class CobblemonChestBlock(settings: Settings) : ChestBlock(settings, {CobblemonBlockEntities.GILDED_CHEST}) {

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = CobblemonChestBlockEntity(pos, state)

    @Deprecated("Deprecated in Java")
    override fun getRenderType(state: BlockState?): BlockRenderType = BlockRenderType.MODEL

}