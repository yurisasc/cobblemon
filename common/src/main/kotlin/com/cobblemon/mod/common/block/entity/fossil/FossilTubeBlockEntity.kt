/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block.entity.fossil

import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.api.multiblock.builder.MultiblockStructureBuilder
import com.cobblemon.mod.common.client.render.models.blockbench.fossil.FossilState
import com.cobblemon.mod.common.util.DataKeys
import net.minecraft.block.BlockState
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
    var fillLevel = 0
    var connectorPosition: Direction? = null
    val state = FossilState()

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener>? {
        return BlockEntityUpdateS2CPacket.create(this)
    }

    override fun toInitialChunkDataNbt(): NbtCompound {
        return createNbt()
    }

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        nbt.putInt(DataKeys.TUBE_FILL_LEVEL, fillLevel)
        if (connectorPosition != null) {
            nbt.putString(DataKeys.DIRECTION, connectorPosition?.name)
        }
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        fillLevel = nbt.getInt(DataKeys.TUBE_FILL_LEVEL)
        connectorPosition = if (nbt.contains(DataKeys.DIRECTION)) {
            Direction.valueOf(nbt.getString(DataKeys.DIRECTION))
        } else {
            null
        }
    }
}