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
import com.cobblemon.mod.common.api.multiblock.builder.MultiblockStructureBuilder
import com.cobblemon.mod.common.util.DataKeys
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.util.math.BlockPos

class FossilCompartmentBlockEntity(
    pos: BlockPos, state: BlockState,
    multiblockBuilder: MultiblockStructureBuilder
): FossilMultiblockEntity(pos, state, multiblockBuilder, CobblemonBlockEntities.FOSSIL_COMPARTMENT) {
    private var insertedFossilStacks = mutableListOf<ItemStack>()

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener>? {
        return BlockEntityUpdateS2CPacket.create(this)
    }

    override fun toInitialChunkDataNbt(): NbtCompound {
        return createNbt()
    }

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)

        if (this.insertedFossilStacks.isNotEmpty()) {
            val fossilStacks = NbtCompound()
            for (i in this.insertedFossilStacks.indices) {
                fossilStacks.put(i.toString(), this.insertedFossilStacks[i].writeNbt(NbtCompound()))
            }
            nbt.put(DataKeys.INSERTED_FOSSIL_STACKS, fossilStacks)
        }
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)

        this.insertedFossilStacks.clear()

        if (nbt.contains(DataKeys.INSERTED_FOSSIL_STACKS)) {
            val fossilStacks = nbt.getCompound(DataKeys.INSERTED_FOSSIL_STACKS)
            for (key in fossilStacks.keys) {
                val fossilStack = ItemStack.fromNbt(fossilStacks.getCompound(key))
                this.insertedFossilStacks.add(fossilStack)
            }
        }
    }

    fun getInsertedFossilStacks(): List<ItemStack> {
        return this.insertedFossilStacks
    }

    fun isFull(): Boolean {
        return this.insertedFossilStacks.size >= Cobblemon.config.maxInsertedFossilItems
    }

    fun isEmpty(): Boolean {
        return this.insertedFossilStacks.isEmpty()
    }

    fun clear() {
        this.insertedFossilStacks.clear()
    }

    fun insertFossilStack(fossilStack: ItemStack) {
        if (this.isFull()) {
            return
        }
        this.insertedFossilStacks.add(fossilStack)

        this.world?.updateListeners(this.pos, this.cachedState, this.cachedState, Block.NOTIFY_LISTENERS)
        this.markDirty()
    }

    fun withdrawLastFossilStack(): ItemStack {
        if (this.insertedFossilStacks.isEmpty()) {
            return ItemStack.EMPTY
        }
        val last = this.insertedFossilStacks.removeLast()
        this.world?.updateListeners(this.pos, this.cachedState, this.cachedState, Block.NOTIFY_LISTENERS)
        this.markDirty()
        return last
    }
}