/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item

import com.cobblemon.mod.common.CobblemonSounds
import net.minecraft.world.level.block.Block
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemUsageContext
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.level.block.DirectionalBlock

class TumblestoneItem(settings: Properties, val block: Block) : Item(settings) {

    override fun useOnBlock(context: ItemUsageContext): InteractionResult {
        if (context.player == null) return InteractionResult.FAIL

        val state = context.world.getBlockState(context.blockPos)
        val world = context.world
        val pos = context.blockPos
        val direction = context.side

        if (state.isFaceSturdy(world, pos, direction)) { // todo (techdaan): ensure this is the right mapping
            if (!world.getBlockState(pos.offset(direction)).isAir) return InteractionResult.FAIL
            if (!context.player!!.isCreative) context.stack.shrink(1)
            world.setBlockAndUpdate(pos.offset(direction), block.defaultBlockState().setValue(DirectionalBlock.FACING, direction))
            world.playSound(null, pos, CobblemonSounds.TUMBLESTONE_PLACE, SoundSource.BLOCKS)
            return InteractionResult.SUCCESS
        }

        return InteractionResult.FAIL
    }

}