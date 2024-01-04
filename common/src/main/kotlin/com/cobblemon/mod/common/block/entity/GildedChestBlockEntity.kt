/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block.entity

import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.block.chest.GildedState
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.entity.ChestBlockEntity
import net.minecraft.block.entity.LootableContainerBlockEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.GenericContainerScreenHandler
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.text.Text
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos

class GildedChestBlockEntity(pos: BlockPos, state: BlockState) : LootableContainerBlockEntity(CobblemonBlockEntities.GILDED_CHEST, pos, state) {
    var inventoryContents: DefaultedList<ItemStack> = DefaultedList.ofSize(NUM_SLOTS, ItemStack.EMPTY)
    val poseableState: GildedState = GildedState()

    override fun getType(): BlockEntityType<*> = CobblemonBlockEntities.GILDED_CHEST
    override fun size() = NUM_SLOTS

    override fun getContainerName() = Text.of("Gilded Chest")
    override fun createScreenHandler(syncId: Int, playerInventory: PlayerInventory?) =
        GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, this);

    override fun getInvStackList(): DefaultedList<ItemStack> = inventoryContents

    override fun setInvStackList(list: DefaultedList<ItemStack>) {
        inventoryContents = list
    }

    companion object {
        val NUM_SLOTS = 27
    }

}