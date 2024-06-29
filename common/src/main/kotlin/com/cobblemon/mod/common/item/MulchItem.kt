/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item

import com.cobblemon.mod.common.api.mulch.MulchVariant
import com.cobblemon.mod.common.api.mulch.Mulchable
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemUsageContext
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.LevelEvent

class MulchItem(val variant: MulchVariant) : CobblemonItem(Properties()) {

    override fun useOnBlock(context: ItemUsageContext): InteractionResult {
        val world = context.world
        val pos = context.blockPos
        if (this.useOnMulchAble(context.stack, world, pos)) {
            // Plays bone meal effect
            if (!world.isClient) {
                world.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, pos, 0)
            }
            return InteractionResult.sidedSuccess(true)
        }
        return InteractionResult.PASS
    }

    private fun useOnMulchAble(stack: ItemStack, world: Level, pos: BlockPos): Boolean {
        val state = world.getBlockState(pos)
        if (state.block is Mulchable) {
            val mulchAble = state.block as? Mulchable ?: return false
            if (world is ServerLevel && mulchAble.canHaveMulchApplied(world, pos, state, this.variant)) {
                mulchAble.applyMulch(world, world.random, pos, state, this.variant)
                stack.shrink(1)
                return true
            }
        }
        return false
    }

}