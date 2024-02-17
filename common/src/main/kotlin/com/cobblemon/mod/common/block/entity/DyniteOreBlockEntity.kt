/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block.entity

import com.cobblemon.mod.common.CobblemonBlockEntities
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.util.math.BlockPos

class DyniteOreBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(CobblemonBlockEntities.DYNITE_ORE, pos, state) {

    companion object {
        internal val TICKER = BlockEntityTicker<DyniteOreBlockEntity> { world, pos, state, blockEntity ->
            if (world.isClient) return@BlockEntityTicker
            // idk if i'll actually need this but i already made it
        }
    }

}