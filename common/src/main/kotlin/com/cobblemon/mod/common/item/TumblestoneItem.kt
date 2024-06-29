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
import net.minecraft.block.FacingBlock
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemUsageContext
import net.minecraft.sound.SoundCategory
import net.minecraft.world.InteractionResult

class TumblestoneItem(settings: Settings, val block: Block) : Item(settings) {

    override fun useOnBlock(context: ItemUsageContext): InteractionResult {
        if (context.player == null) return InteractionResult.FAIL

        val state = context.world.getBlockState(context.blockPos)
        val world = context.world
        val pos = context.blockPos
        val direction = context.side

        if (state.isSideSolidFullSquare(world, pos, direction)) {
            if (!world.getBlockState(pos.offset(direction)).isAir) return InteractionResult.FAIL
            if (!context.player!!.isCreative) context.stack.shrink(1)
            world.setBlockState(pos.offset(direction), block.defaultState.with(FacingBlock.FACING, direction))
            world.playSound(null, pos, CobblemonSounds.TUMBLESTONE_PLACE, SoundCategory.BLOCKS)
            return InteractionResult.SUCCESS
        }

        return InteractionResult.FAIL
    }

}