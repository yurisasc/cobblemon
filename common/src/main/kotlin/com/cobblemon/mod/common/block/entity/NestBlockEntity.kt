/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block.entity

import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.api.pokemon.breeding.Egg
import com.cobblemon.mod.common.util.DataKeys
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.ItemEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.util.math.BlockPos


class NestBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(CobblemonBlockEntities.NEST, pos, state) {
    var egg: Egg? = null
    var renderState: BlockEntityRenderState? = null

    fun dropEgg() {
        egg?.let {
            val itemEntity = ItemEntity(
                world,
                pos.x.toDouble(),
                pos.y.toDouble(),
                pos.z.toDouble(),
                egg!!.asItemStack()
            )
            world?.spawnEntity(itemEntity)
        }

    }

    fun onUse() {
        dropEgg()
    }

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        if (egg != null) {
            nbt.put(DataKeys.EGG, egg?.toNbt())
        }
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        if (nbt.contains(DataKeys.EGG)) {
            egg = Egg.fromNbt(nbt.getCompound(DataKeys.EGG))
        }
        renderState?.needsRebuild = true
    }

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener> {
        return BlockEntityUpdateS2CPacket.create(this)
    }

    override fun toInitialChunkDataNbt(): NbtCompound {
        return createNbt()
    }

}