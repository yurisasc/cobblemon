/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block.entity.fossil

import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.api.fossil.NaturalMaterials
import com.cobblemon.mod.common.api.multiblock.builder.MultiblockStructureBuilder
import com.cobblemon.mod.common.block.multiblock.FossilMultiblockStructure
import com.cobblemon.mod.common.client.render.models.blockbench.fossil.FossilState
import com.cobblemon.mod.common.util.DataKeys
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

class FossilTubeBlockEntity(
    pos: BlockPos, state: BlockState,
    multiblockBuilder: MultiblockStructureBuilder
) : FossilMultiblockEntity(pos, state, multiblockBuilder, CobblemonBlockEntities.FOSSIL_TUBE) {
    val inv = FossilTubeInventory(this)

    class FossilTubeInventory(val tubeEntity: FossilTubeBlockEntity) : SidedInventory {
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
            if (tubeEntity.multiblockStructure != null) {
                val struct = tubeEntity.multiblockStructure as FossilMultiblockStructure
                tubeEntity.world?.let {
                    struct.insertOrganicMaterial(stack, it)
                }
            }
        }

        override fun markDirty() {
            if (tubeEntity.world != null) {
                tubeEntity.multiblockStructure?.markDirty(tubeEntity.world!!)
            }
        }

        override fun canPlayerUse(player: PlayerEntity?): Boolean {
            return false
        }

        override fun getAvailableSlots(side: Direction?): IntArray {
            return IntArray(1)
        }

        override fun canInsert(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
            if (tubeEntity.multiblockStructure is FossilMultiblockStructure) {
                val structure = tubeEntity.multiblockStructure as FossilMultiblockStructure
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