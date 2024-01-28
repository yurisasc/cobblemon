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
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

class RestorationTankBlockEntity(
    pos: BlockPos, state: BlockState,
    multiblockBuilder: MultiblockStructureBuilder
) : FossilMultiblockEntity(pos, state, multiblockBuilder, CobblemonBlockEntities.RESTORATION_TANK) {
    val inv = RestorationTankInventory(this)

    override fun markRemoved() {
        super.markRemoved()
        if(this.multiblockStructure != null && world != null) {
            this.multiblockStructure!!.markRemoved(world!!)
        }
    }
    class RestorationTankInventory(val tankEntity: RestorationTankBlockEntity) : SidedInventory {
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
            if (tankEntity.multiblockStructure != null) {
                val struct = tankEntity.multiblockStructure as FossilMultiblockStructure
                tankEntity.world?.let {
                    struct.insertOrganicMaterial(stack, it)
                }
            }
        }

        override fun markDirty() {
            if (tankEntity.world != null) {
                tankEntity.multiblockStructure?.markDirty(tankEntity.world!!)
            }
        }

        override fun canPlayerUse(player: PlayerEntity?): Boolean {
            return false
        }

        override fun getAvailableSlots(side: Direction?): IntArray {
            return IntArray(1)
        }

        override fun canInsert(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
            if (tankEntity.multiblockStructure is FossilMultiblockStructure) {
                val structure = tankEntity.multiblockStructure as FossilMultiblockStructure
                return stack?.let { NaturalMaterials.isNaturalMaterial(it) } == true
                        && structure.organicMaterialInside < FossilMultiblockStructure.MATERIAL_TO_START
                        && structure.createdPokemon == null
            }
            return false
        }

        override fun canExtract(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
            return false
        }

    }
}