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
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SidedInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.registry.Registries
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

class RestorationTankBlockEntity(
    pos: BlockPos, state: BlockState,
    multiblockBuilder: MultiblockStructureBuilder
) : FossilMultiblockEntity(pos, state, multiblockBuilder, CobblemonBlockEntities.RESTORATION_TANK)  {
    val inv = RestorationTankInventory(this)

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        nbt.put("inventory", inv.toNbtList())
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        inv.readNbtList( nbt.get("inventory") as NbtList)
    }

    class RestorationTankInventory(val tankEntity: RestorationTankBlockEntity) : SimpleInventory(8), SidedInventory {

        override fun markDirty() {
            super.markDirty()
            for(i in 0..size()) {
                val itemStack : ItemStack = this.getStack(i)
                if(!itemStack.isEmpty) {
                    val struct = tankEntity.multiblockStructure as? FossilMultiblockStructure
                    val returnIdentifier = NaturalMaterials.getReturnItem(itemStack)

                    if(tankEntity.world != null) {
                        if(struct?.insertOrganicMaterial(itemStack, tankEntity.world!!) == true) {
                            removeStack(i)
                            if (returnIdentifier != null) {
                                val returnItem = Registries.ITEM.get(returnIdentifier)
                                setStack(i, ItemStack(returnItem, itemStack.count))
                            }
                        }
                    }
                }
            }

            tankEntity.world?.let {
                tankEntity.world!!.updateListeners(tankEntity.pos, tankEntity.cachedState, tankEntity.cachedState, Block.NOTIFY_ALL)
                tankEntity.world!!.updateComparators(tankEntity.pos, tankEntity.world!!.getBlockState(tankEntity.pos).block)
                tankEntity.markDirty()
                tankEntity.multiblockStructure?.markDirty(it)
            }
        }

        override fun canPlayerUse(player: PlayerEntity?): Boolean {
            return false
        }

        override fun getAvailableSlots(side: Direction?): IntArray {
            return intArrayOf(0,1,2,3,4,5,6,7)
        }

        override fun canInsert(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
            if (tankEntity.multiblockStructure is FossilMultiblockStructure) {
                if(dir != Direction.DOWN) {
                    val structure = tankEntity.multiblockStructure as FossilMultiblockStructure
                    val canUtilize = stack?.let { NaturalMaterials.isNaturalMaterial(it) } == true
                            && structure.organicMaterialInside < FossilMultiblockStructure.MATERIAL_TO_START
                            && structure.createdPokemon == null && super.canInsert(stack)
                    val returnItem = NaturalMaterials.getReturnItem(stack!!) ?: return canUtilize
                    if(canUtilize) {
                        // See if there's room
                        return super.canInsert(ItemStack( Registries.ITEM.get(returnItem)))
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