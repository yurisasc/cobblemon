/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block.entity.fossil

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.api.fossil.Fossils
import com.cobblemon.mod.common.api.multiblock.builder.MultiblockStructureBuilder
import com.cobblemon.mod.common.block.multiblock.FossilMultiblockStructure
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

class FossilCompartmentBlockEntity(
    pos: BlockPos, state: BlockState,
    multiblockBuilder: MultiblockStructureBuilder
): FossilMultiblockEntity(pos, state, multiblockBuilder, CobblemonBlockEntities.FOSSIL_COMPARTMENT) {

    val inv = FossilCompartmentInventory(this)

    class FossilCompartmentInventory(val compartmentEntity: FossilCompartmentBlockEntity) : SidedInventory {
        override fun clear() {
            TODO("Not yet implemented")
        }

        override fun size(): Int {
            TODO("Not yet implemented")
        }

        override fun isEmpty(): Boolean {
            return false
        }

        override fun getStack(slot: Int): ItemStack {
            return ItemStack.EMPTY
        }

        override fun removeStack(slot: Int, amount: Int): ItemStack {
            return ItemStack.EMPTY
        }

        override fun removeStack(slot: Int): ItemStack {
            return ItemStack.EMPTY
        }

        override fun setStack(slot: Int, stack: ItemStack) {
            if (compartmentEntity.multiblockStructure != null) {
                val struct = compartmentEntity.multiblockStructure as FossilMultiblockStructure
                compartmentEntity.world?.let {
                    struct.insertFossil(stack, it)
                }
            }
        }

        override fun markDirty() {
            if (compartmentEntity.world != null) {
                compartmentEntity.multiblockStructure?.markDirty(compartmentEntity.world!!)
            }
        }

        override fun canPlayerUse(player: PlayerEntity?): Boolean {
            return false
        }

        override fun getAvailableSlots(side: Direction?): IntArray {
            return IntArray(1)
        }

        override fun canInsert(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
            if (compartmentEntity.multiblockStructure is FossilMultiblockStructure) {
                val structure = compartmentEntity.multiblockStructure as FossilMultiblockStructure
                return stack?.let { Fossils.isFossilIngredient(it) } == true
                        && structure.fossilInventory.size < Cobblemon.config.maxInsertedFossilItems
                        && structure.isRunning() == false && structure.resultingFossil == null
                        && Fossils.getSubFossilByItemStacks( structure.fossilInventory + mutableListOf(stack) ) != null
            }
            return false
        }

        override fun canExtract(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
            return false
        }

    }
}