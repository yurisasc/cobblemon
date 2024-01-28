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
import com.cobblemon.mod.common.api.tms.TechnicalMachines
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.gui.TMMScreenHandler
import net.minecraft.block.BlockState
import net.minecraft.block.entity.LockableContainerBlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.registry.Registries
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction


class TMBlockEntity(
        val blockPos: BlockPos,
        val blockState: BlockState
    )  : LockableContainerBlockEntity(CobblemonBlockEntities.TM_BLOCK, blockPos, blockState) {
    var tmmInventory = TMBlockInventory(this)
    var automationDelay: Int = AUTOMATION_DELAY
    var partialTicks = 0.0f
    companion object {
        const val AUTOMATION_DELAY = 4
        const val FILTER_TM_NBT = "FilterTM"
    }
    //var filterTM: TechnicalMachine? = null
    //private var inventory: DefaultedList<ItemStack> = DefaultedList.ofSize(size(), ItemStack.EMPTY)


    /*override fun size(): Int { return 3 }
    override fun getContainerName(): Text {
        return Text.translatable("container.tmblock")
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

    override fun toInitialChunkDataNbt(): NbtCompound {
        val res = NbtCompound()
        writeNbt(res)
        return res
    }

    override fun clear() {
        tmmInventory.clear()
    }

    override fun size(): Int {
        return this.tmmInventory.size()
    }

    override fun isEmpty(): Boolean {
        return this.tmmInventory.isEmpty()
    }

    override fun getStack(slot: Int): ItemStack {
        return this.tmmInventory.getStack(slot)
    }

    override fun removeStack(slot: Int, amount: Int): ItemStack {
        return this.tmmInventory.removeStack(slot, amount)
    }

    override fun removeStack(slot: Int): ItemStack {
        return this.tmmInventory.removeStack(slot)
    }

    override fun setStack(slot: Int, stack: ItemStack?) {
        return this.tmmInventory.setStack(slot, stack)
    }

    override fun canPlayerUse(player: PlayerEntity?): Boolean {
        return this.tmmInventory.canPlayerUse(player)
    }

    override fun getContainerName(): Text {
        return Text.translatable("container.brewing")
    }


    override fun createScreenHandler(syncId: Int, playerInventory: PlayerInventory?): ScreenHandler {
        return TMMScreenHandler(syncId, playerInventory!!, this.tmmInventory)
    }

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener>? {
        return BlockEntityUpdateS2CPacket.create(this)
    }

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        Inventories.writeNbt(nbt, tmmInventory.items)
        val filterTmCompound = tmmInventory.filterTM?.writeNbt(NbtCompound())
        if (filterTmCompound != null)
            nbt.put(FILTER_TM_NBT, filterTmCompound)
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        Inventories.readNbt(nbt, tmmInventory.items)
        if (nbt.contains(FILTER_TM_NBT)) {
            val itemStack = CobblemonItems.TECHNICAL_MACHINE.defaultStack

            tmmInventory.filterTM = ItemStack.fromNbt(nbt.getCompound(FILTER_TM_NBT))

            val test = 2
        }

    }

    class TMBlockInventory(val tmBlockEntity: TMBlockEntity) : SidedInventory {

        private val BLANK_DISC_SLOT_INDEX = 0
        private val GEM_SLOT_INDEX = 1
        private val MISC_SLOT_INDEX = 2
        private val OUTPUT_SLOT_INDEX = 3
        private val INPUT_SLOTS = intArrayOf(0, 1, 2, 3)

        var filterTM: ItemStack? = null
        //var inventory: MutableMap<Int, ItemStack?> = mutableMapOf(0 to ItemStack.EMPTY, 1 to ItemStack.EMPTY, 2 to ItemStack.EMPTY)
        var items: DefaultedList<ItemStack?>? = DefaultedList.ofSize(4, ItemStack.EMPTY)

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
            return if (side == Direction.DOWN) {
                return listOf(this.OUTPUT_SLOT_INDEX).toIntArray()
            }
            else this.INPUT_SLOTS


//            val result = this.items?.size?.let { IntArray(it) }
//            if (result != null) {
//                for (i in result.indices) {
//                    result[i] = i
//                }
//            }
//            return result
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
            tmBlockEntity.markDirty()
        }

        override fun markDirty() {
            if (tmBlockEntity.world != null)
                tmBlockEntity.markDirty()
        }

        override fun canPlayerUse(player: PlayerEntity?): Boolean {
            return Inventory.canPlayerUse(this.tmBlockEntity, player)
        }

        /*override fun getAvailableSlots(side: Direction?): IntArray {
            return IntArray(3)
        }*/

        override fun canInsert(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
            // todo only allow for hopper to insert materials if it has a filterTM in it
            if (stack != null && slot < this.INPUT_SLOTS.size) {
                val tm = TechnicalMachines.getTechnicalMachineFromStack(tmBlockEntity.tmmInventory.filterTM)
                // if material is needed then load it
                if (tmBlockEntity.materialNeeded(tm, stack) && (ItemStack.areEqual(items?.get(slot) ?: ItemStack.EMPTY, ItemStack.EMPTY) || ItemStack.areItemsEqual(items?.get(slot) ?: ItemStack.EMPTY, stack))) {
                    //tmBlockEntity.loadMaterial(stack)
                    return true
                }
                else
                    return false
            }
            return false
        }

        override fun canExtract(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
            return dir == Direction.DOWN && slot == this.OUTPUT_SLOT_INDEX
        }

    }

}