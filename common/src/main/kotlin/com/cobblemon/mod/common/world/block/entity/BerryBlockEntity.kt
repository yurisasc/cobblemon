/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.world.block.entity

import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.world.block.BerryBlock
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos

class BerryBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(CobblemonBlockEntities.BERRY.get(), pos, state) {

    fun berryBlock() = this.cachedState.block as BerryBlock

    fun berry() = this.berryBlock().berry

    override fun readNbt(nbt: NbtCompound?) {
        super.readNbt(nbt)
    }

    override fun writeNbt(nbt: NbtCompound?) {
        super.writeNbt(nbt)
    }
}