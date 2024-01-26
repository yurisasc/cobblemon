/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block.entity

import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.api.tms.TechnicalMachine
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.gui.TMMScreenHandler
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.entity.LootableContainerBlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.screen.GenericContainerScreenHandler
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.text.Text
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction


class TMBlockEntity(
        val blockPos: BlockPos,
        val blockState: BlockState
    )  : /*LootableContainer*/BlockEntity(CobblemonBlockEntities.TM_BLOCK, blockPos, blockState) {
    var tmmInventory = TMBlockInventory(this)
    //var filterTM: TechnicalMachine? = null
    //private var inventory: DefaultedList<ItemStack> = DefaultedList.ofSize(size(), ItemStack.EMPTY)


    /*override fun size(): Int { return 3 }
    override fun getContainerName(): Text {
        return Text.translatable("container.tmblock")
    }

    override fun createScreenHandler(syncId: Int, playerInventory: PlayerInventory?): ScreenHandler {
        return TMMScreenHandler(syncId)
    }

    override fun getInvStackList(): DefaultedList<ItemStack> {
        return inventory
    }

    override fun setInvStackList(list: DefaultedList<ItemStack>?) {
        if (list != null) {
            this.inventory = list
        }
    }*/

    fun materialNeeded(tm: TechnicalMachine, itemStack: ItemStack): Boolean {
        val typeGem = Registries.ITEM.get(ElementalTypes.get(tm.type)?.typeGem).defaultStack
        val recipeItem = Registries.ITEM.get(tm.recipe?.item)
        val inventory = tmmInventory.inventory

        //inventory.count { it.value == CobblemonItems.BLANK_TM.defaultStack }
        //ItemStack.areEqual()

        // if blank TM is needed still
        if ((inventory.none { it.value?.count == 1 && it.value?.item == CobblemonItems.BLANK_TM }) && ItemStack.areEqual(itemStack, ItemStack(CobblemonItems.BLANK_TM, 1)))
            return true // load in blank TM
        // if type gem is needed still
        else if (inventory.none { ItemStack.areEqual(it.value, typeGem) } && ItemStack.areEqual(itemStack, typeGem))
            return true // load in Type Gem
        // if the itemStack is listed as needed in the recipe and the count is not currently met
        else return inventory.none { it.value?.count == tm.recipe?.count && ItemStack.areItemsEqual(it.value, ItemStack(recipeItem, 1)) } && ItemStack.areEqual(itemStack, ItemStack(recipeItem, 1))
    }

    class TMBlockInventory(val tmBlockEntity: TMBlockEntity) : SidedInventory {
        var filterTM: TechnicalMachine? = null
        var previousFilterTM: TechnicalMachine? = null
        var inventory: MutableMap<Int, ItemStack?> = mutableMapOf(0 to ItemStack.EMPTY, 1 to ItemStack.EMPTY, 2 to ItemStack.EMPTY)

        override fun clear() {
            TODO("Not yet implemented")
        }

        override fun size(): Int {
            return 3
        }

        override fun isEmpty(): Boolean {
            return !inventory.any { it.value != ItemStack.EMPTY }
        }

        override fun getStack(slot: Int): ItemStack {
            return inventory[slot] ?: ItemStack.EMPTY
        }

        override fun removeStack(slot: Int, amount: Int): ItemStack {
            val slotStack = inventory[slot]
            inventory[slot]?.decrement(amount)
            return slotStack ?: ItemStack.EMPTY
        }

        override fun removeStack(slot: Int): ItemStack {
            val slotStack = inventory[slot]
            inventory[slot] = ItemStack.EMPTY
            return slotStack ?: ItemStack.EMPTY
        }

        override fun setStack(slot: Int, stack: ItemStack?) {
            inventory[slot] = stack
        }

        override fun markDirty() {
            if (tmBlockEntity.world != null)
                tmBlockEntity.markDirty()
        }

        override fun canPlayerUse(player: PlayerEntity?): Boolean {
            return false
        }

        override fun getAvailableSlots(side: Direction?): IntArray {
            return IntArray(3)
        }

        override fun canInsert(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
            // todo only allow for hopper to insert materials if it has a filterTM in it
            if (filterTM != null && stack != null) {

                // if material is needed then load it
                if (tmBlockEntity.materialNeeded(filterTM!!, stack) && (ItemStack.areEqual(inventory[slot], ItemStack.EMPTY) || ItemStack.areItemsEqual(inventory[slot], stack))) {
                    //tmBlockEntity.loadMaterial(stack)
                    return true
                }
                else
                    return false
            }
            return false
        }

        override fun canExtract(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
            return false
        }

    }

}