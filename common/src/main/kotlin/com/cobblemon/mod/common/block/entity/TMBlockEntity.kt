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
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World


class TMBlockEntity(
        val blockPos: BlockPos,
        val blockState: BlockState
    )  : /*LootableContainer*/BlockEntity(CobblemonBlockEntities.TM_BLOCK, blockPos, blockState) {
    var tmmInventory = TMBlockInventory(this)
    val AUTOMATION_DELAY = 1
    var automationDelay: Int = AUTOMATION_DELAY
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

    fun resetAutomationDelay() {
        automationDelay = AUTOMATION_DELAY
    }


    fun materialNeeded(tm: TechnicalMachine?, itemStack: ItemStack): Boolean {
        val typeGem = Registries.ITEM.get(tm?.type?.let { ElementalTypes.get(it)?.typeGem }).defaultStack
        val recipeItem = Registries.ITEM.get(tm?.recipe?.item)
        //val inventory = tmmInventory.inventory
        val inventory = tmmInventory.items

        //inventory.count { it.value == CobblemonItems.BLANK_TM.defaultStack }
        //ItemStack.areEqual()


        if (inventory != null) {
            if (tmmInventory.filterTM != null) {
                // if blank TM is needed still
                if ((inventory.none { it?.count == 1 && it.item == CobblemonItems.BLANK_TM }) && ItemStack.areEqual(itemStack, ItemStack(CobblemonItems.BLANK_TM, 1)))
                    return true // load in blank TM
                // if type gem is needed still
                else if (inventory.none { ItemStack.areEqual(it, typeGem) } && ItemStack.areEqual(itemStack, typeGem))
                    return true // load in Type Gem
                // if the itemStack is listed as needed in the recipe and the count is not currently met
                else if (inventory.none { it?.count == tm?.recipe?.count && ItemStack.areItemsEqual(it, ItemStack(recipeItem, 1)) } && ItemStack.areEqual(itemStack, ItemStack(recipeItem, 1)))
                    return true
                else
                    return false
            }
            else if (tmmInventory.filterTM == null && ItemStack.areItemsEqual(itemStack,Items.AMETHYST_SHARD.defaultStack)) {
                return inventory.none { it?.count == 1 && ItemStack.areItemsEqual(it, Items.AMETHYST_SHARD.defaultStack) }
            }
        }
        return false
    }

    class TMBlockInventory(val tmBlockEntity: TMBlockEntity) : SidedInventory {
        var filterTM: TechnicalMachine? = null
        var previousFilterTM: TechnicalMachine? = null
        //var inventory: MutableMap<Int, ItemStack?> = mutableMapOf(0 to ItemStack.EMPTY, 1 to ItemStack.EMPTY, 2 to ItemStack.EMPTY)
        var items: DefaultedList<ItemStack?>? = DefaultedList.ofSize(3, ItemStack.EMPTY)

        fun hasFilterTM(): Boolean {
            return filterTM != null
        }

        override fun clear() {
            this.items?.clear()
        }

        override fun size(): Int {
            return this.items?.size ?: 0;
        }

        /*fun getInvSize(): Int {
            return size()
        }*/

        fun getInvStack(slot: Int): ItemStack {
            return items?.get(slot) ?: ItemStack.EMPTY
        }

        override fun isEmpty(): Boolean {
            for (i in 0 until size()) {
                val stack: ItemStack = getInvStack(i)
                if (!stack.isEmpty) {
                    return false
                }
            }
            return true
        }

        /*override fun isEmpty(): Boolean {
            return !inventory.any { it.value != ItemStack.EMPTY }
        }*/

        override fun getStack(slot: Int): ItemStack {
            return items?.get(slot) ?: ItemStack.EMPTY
        }

        override fun getAvailableSlots(side: Direction?): IntArray? {
            val result = this.items?.size?.let { IntArray(it) }
            if (result != null) {
                for (i in result.indices) {
                    result[i] = i
                }
            }
            return result
        }

        override fun removeStack(slot: Int, amount: Int): ItemStack {
            val slotStack = items?.get(slot)
            items?.get(slot)?.decrement(amount)
            return slotStack ?: ItemStack.EMPTY
        }

        override fun removeStack(slot: Int): ItemStack {
            val slotStack = items?.get(slot)
            items?.set(slot, ItemStack.EMPTY)
            return slotStack ?: ItemStack.EMPTY
        }

        override fun setStack(slot: Int, stack: ItemStack?) {
            items?.set(slot, stack)
        }

        override fun markDirty() {
            if (tmBlockEntity.world != null)
                tmBlockEntity.markDirty()
        }

        override fun canPlayerUse(player: PlayerEntity?): Boolean {
            return false
        }

        /*override fun getAvailableSlots(side: Direction?): IntArray {
            return IntArray(3)
        }*/

        override fun canInsert(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
            // todo only allow for hopper to insert materials if it has a filterTM in it
            if (stack != null) {
                // if material is needed then load it
                if (tmBlockEntity.materialNeeded(filterTM, stack) && (ItemStack.areEqual(items?.get(slot) ?: ItemStack.EMPTY, ItemStack.EMPTY) || ItemStack.areItemsEqual(items?.get(slot) ?: ItemStack.EMPTY, stack))) {
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