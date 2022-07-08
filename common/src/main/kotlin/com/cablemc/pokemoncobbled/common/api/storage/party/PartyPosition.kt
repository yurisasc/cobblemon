package com.cablemc.pokemoncobbled.common.api.storage.party

import com.cablemc.pokemoncobbled.common.api.storage.StorePosition
import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.util.readSizedInt
import com.cablemc.pokemoncobbled.common.util.writeSizedInt
import net.minecraft.network.PacketByteBuf

data class PartyPosition(val slot: Int) : StorePosition {
    companion object {
        fun PacketByteBuf.writePartyPosition(position: PartyPosition) {
            writeSizedInt(IntSize.U_BYTE, position.slot)
        }
        fun PacketByteBuf.readPartyPosition() = PartyPosition(readSizedInt(IntSize.U_BYTE))
    }
}