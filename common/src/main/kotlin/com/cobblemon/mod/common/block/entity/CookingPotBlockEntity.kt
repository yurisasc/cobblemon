/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block.entity

import com.cobblemon.mod.common.gui.CookingPotScreenHandler
import net.minecraft.block.BlockState
import net.minecraft.block.entity.AbstractFurnaceBlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.recipe.RecipeType
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos


class CookingPotBlockEntity(pos: BlockPos?, state: BlockState?) :
    AbstractFurnaceBlockEntity(BlockEntityType.FURNACE, pos, state, RecipeType.SMELTING) {
    override fun getContainerName(): Text {
        return Text.translatable("container.furnace")
    }

    override fun createScreenHandler(syncId: Int, playerInventory: PlayerInventory): ScreenHandler {
        return CookingPotScreenHandler(syncId, playerInventory, this, this.propertyDelegate)
    }
}