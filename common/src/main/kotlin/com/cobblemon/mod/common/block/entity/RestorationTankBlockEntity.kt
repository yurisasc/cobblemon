/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block.entity

import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.api.fossil.NaturalMaterials
import com.cobblemon.mod.common.api.multiblock.builder.MultiblockStructureBuilder
import com.cobblemon.mod.common.block.multiblock.FossilMultiblockStructure
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.Registries
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

class RestorationTankBlockEntity(
    pos: BlockPos, state: BlockState,
    multiblockBuilder: MultiblockStructureBuilder
) : FossilMultiblockEntity(pos, state, multiblockBuilder, CobblemonBlockEntities.RESTORATION_TANK) {
    val inv = RestorationTankInventory(this)

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        Inventories.writeNbt(nbt, inv.items)
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        Inventories.readNbt(nbt, inv.items)
    }

    class RestorationTankInventory(val tankEntity: RestorationTankBlockEntity) : SidedInventory {

        val items: DefaultedList<ItemStack> =  DefaultedList.ofSize(8, ItemStack.EMPTY)


        override fun clear() {
            for (i in items.indices) {
                items[i] = ItemStack.EMPTY
            }
        }

        override fun size(): Int {
            return 8
        }

        override fun isEmpty(): Boolean {
            return items.all { it == ItemStack.EMPTY }
        }

        override fun getStack(slot: Int): ItemStack {
            if(slot == -1 || slot >= size()) {
                return ItemStack.EMPTY
            }
            return items[slot]
        }

        override fun removeStack(slot: Int, amount: Int): ItemStack {
            if(slot < 0 || slot >= size()) {
                return ItemStack.EMPTY
            }
            val result = items[slot].split(amount)
            if(items[slot].count == 0) {
                items[slot] = ItemStack.EMPTY
            }
            return result
        }

        override fun removeStack(slot: Int): ItemStack {
            return removeStack(slot, 1)
        }

        override fun setStack(slot: Int, stack: ItemStack) {
            val struct = tankEntity.multiblockStructure as? FossilMultiblockStructure
            tankEntity.world?.let {
                struct?.insertOrganicMaterial(stack, it)
                val returnIdentifier = NaturalMaterials.getReturnItem((stack))
                if(returnIdentifier != null ) {
                    // Store the return item
                    val returnItem = Registries.ITEM.get(returnIdentifier)
                    storeReturnItem(returnItem, stack.count)
                }
            }
        }

        private fun storeReturnItem(returnItem: Item, count: Int) {
            val destStack = items.withIndex().firstOrNull {
                it.value == ItemStack.EMPTY || (it.value.count < it.value.maxCount && it.value.item == returnItem)
            }
            if (destStack != null) {
                if(destStack.value == ItemStack.EMPTY) {
                    items[destStack.index] = ItemStack(returnItem, 1)
                } else {
                    destStack.value.increment(count)
                }
            }
        }

        override fun markDirty() {
            tankEntity.world?.let {
                tankEntity.multiblockStructure?.markDirty(it)
            }
        }

        override fun canPlayerUse(player: PlayerEntity?): Boolean {
            return false
        }

        override fun getAvailableSlots(side: Direction?): IntArray {
            return if(side == Direction.DOWN)  intArrayOf(0,1,2,3,4,5,6,7) else intArrayOf(-1)
        }

        override fun canInsert(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
            if (tankEntity.multiblockStructure is FossilMultiblockStructure) {
                if(dir != Direction.DOWN) {
                    val structure = tankEntity.multiblockStructure as FossilMultiblockStructure
                    val canUtilize = stack?.let { NaturalMaterials.isNaturalMaterial(it) } == true
                            && structure.organicMaterialInside < FossilMultiblockStructure.MATERIAL_TO_START
                            && structure.createdPokemon == null
                    val returnItem = NaturalMaterials.getReturnItem(stack!!) ?: return false
                    if(canUtilize) {
                        // See if there's room
                        for (i in items.indices ) {
                            if(items[i] == ItemStack.EMPTY || (items[i].count < items[i].maxCount
                                            && items[i].item == Registries.ITEM.get(returnItem))) {
                                return true
                            }
                        }
                        return false
                    }
                }
            }
            return false
        }

        override fun canExtract(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
            return dir == Direction.DOWN && slot >= 0 && slot < size()
        }

    }
}