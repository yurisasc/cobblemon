package com.cablemc.pokemoncobbled.common.api.storage.pc

import com.cablemc.pokemoncobbled.common.api.storage.StorePosition
import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.util.readSizedInt
import com.cablemc.pokemoncobbled.common.util.writeSizedInt
import net.minecraft.network.PacketByteBuf

data class PCPosition(val box: Int, val slot: Int) : StorePosition {
    companion object {
        fun PacketByteBuf.writePCPosition(position: PCPosition) {
            writeSizedInt(IntSize.U_BYTE, position.box)
            writeSizedInt(IntSize.U_BYTE, position.slot)
        }
        fun PacketByteBuf.readPCPosition() = PCPosition(readSizedInt(IntSize.U_BYTE), readSizedInt(IntSize.U_BYTE))
    }
}