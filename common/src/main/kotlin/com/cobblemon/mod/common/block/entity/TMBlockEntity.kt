/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block.entity

import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.api.tms.TechnicalMachine
import com.cobblemon.mod.common.block.TMBlock
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction


class TMBlockEntity(
    val blockPos: BlockPos,
    val blockState: BlockState
) {
    val inv = TMBlockInventory(this)
    var filterTM: TechnicalMachine? = null

    class TMBlockInventory(val tmBlockEntity: TMBlockEntity) : SidedInventory {
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
            TODO("Not yet implemented")
        }

        override fun setStack(slot: Int, stack: ItemStack?) {
            TODO("Not yet implemented")
        }

        override fun markDirty() {
            TODO("Not yet implemented")
        }

        override fun canPlayerUse(player: PlayerEntity?): Boolean {
            return false
        }

        override fun getAvailableSlots(side: Direction?): IntArray {
            return IntArray(1)
        }

        override fun canInsert(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
            // todo only allow for hopper to insert materials if it has a filterTM in it
            /*if (filterTM != null && stack != null) {

                // if material is needed then load it
                if (tmBlock.materialNeeded(tmBlock.filterTM!!, stack)) {
                    tmBlock.loadMaterial(stack)
                    return true
                }
                else
                    return false
            }*/
            return false
        }

        override fun canExtract(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
            return false
        }

    }

}