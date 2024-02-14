/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.api.pokemon.breeding.Egg
import com.cobblemon.mod.common.block.entity.EggBlockEntity
import com.cobblemon.mod.common.util.DataKeys
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class EggBlock(settings: Settings?) : BlockWithEntity(settings) {
    override fun onPlaced(
        world: World?,
        pos: BlockPos?,
        state: BlockState?,
        placer: LivingEntity?,
        itemStack: ItemStack
    ) {
        val entity = world?.getBlockEntity(pos) as? EggBlockEntity
        val itemBlockEntityNbt = BlockItem.getBlockEntityNbt(itemStack) ?: return
        entity?.let { entity.readNbt(itemBlockEntityNbt) }
        super.onPlaced(world, pos, state, placer, itemStack)
    }
    override fun getRenderType(state: BlockState?) = BlockRenderType.ENTITYBLOCK_ANIMATED
    override fun createBlockEntity(pos: BlockPos, state: BlockState) = EggBlockEntity(pos, state)
}