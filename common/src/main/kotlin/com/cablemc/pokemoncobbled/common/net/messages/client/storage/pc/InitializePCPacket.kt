package com.cablemc.pokemoncobbled.common.net.messages.client.storage.pc

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.api.storage.pc.PCStore
import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.util.readSizedInt
import com.cablemc.pokemoncobbled.common.util.writeSizedInt
import net.minecraft.network.PacketByteBuf
import java.util.UUID

/**
 * Initializes a client side representation of a PC. It is given the ID, the number of boxes,
 * and whether overflow has occurred.
 *
 * Handled by [com.cablemc.pokemoncobbled.common.client.net.storage.pc.InitializePCHandler].
 *
 * @author Hiroku
 * @since June 18th, 2022
 */
class InitializePCPacket() : NetworkPacket {
    lateinit var storeID: UUID
    var boxCount = 0
    /* Might be useful to know this in case we want the option to restore the overflow as a button in PC GUI. */
    var hasOverflowed = false

    constructor(pc: PCStore): this() {
        this.storeID = pc.uuid
        this.boxCount = pc.boxes.size
        this.hasOverflowed = pc.backupStore.any()
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUuid(storeID)
        buffer.writeSizedInt(IntSize.U_BYTE, boxCount)
        buffer.writeBoolean(hasOverflowed)
    }

    override fun decode(buffer: PacketByteBuf) {
        storeID = buffer.readUuid()
        boxCount = buffer.readSizedInt(IntSize.U_BYTE)
        hasOverflowed = buffer.readBoolean()
    }
}