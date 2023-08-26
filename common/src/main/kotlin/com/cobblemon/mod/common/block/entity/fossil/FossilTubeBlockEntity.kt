package com.cobblemon.mod.common.block.entity.fossil

import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.block.multiblock.builder.MultiblockStructureBuilder
import com.cobblemon.mod.common.util.DataKeys
import net.minecraft.block.BlockState
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.util.math.BlockPos

class FossilTubeBlockEntity(
    pos: BlockPos, state: BlockState,
    multiblockBuilder: MultiblockStructureBuilder
) : FossilMultiblockEntity(pos, state, multiblockBuilder, CobblemonBlockEntities.FOSSIL_TUBE) {
    var fillLevel = 0

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener>? {
        return BlockEntityUpdateS2CPacket.create(this) {
            it.createNbt()
        }
    }

    override fun toInitialChunkDataNbt(): NbtCompound {
        return createNbt()
    }

    override fun writeNbt(nbt: NbtCompound?) {
        super.writeNbt(nbt)
        nbt?.putInt(DataKeys.TUBE_FILL_LEVEL, fillLevel)
    }

    override fun readNbt(nbt: NbtCompound?) {
        super.readNbt(nbt)
        fillLevel = nbt?.getInt(DataKeys.TUBE_FILL_LEVEL) ?: 0
        this.markDirty()
    }
}