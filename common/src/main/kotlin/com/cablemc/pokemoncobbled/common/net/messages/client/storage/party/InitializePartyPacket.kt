package com.cablemc.pokemoncobbled.common.net.messages.client.storage.party

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.util.readSizedInt
import com.cablemc.pokemoncobbled.common.util.writeSizedInt
import net.minecraft.network.PacketByteBuf
import java.util.UUID

/**
 * Creates a party on the client side with the given UUID and slot count.
 *
 * This can be used for immediately telling the client that this is their party to use
 * in overlay rendering, but generally is just necessary before sending Pokémon updates
 * targeting this store.
 *
 * Handled by [com.cablemc.pokemoncobbled.common.client.net.storage.party.InitializePartyHandler]
 *
 * @author Hiroku
 * @since November 29th, 2021
 */
class InitializePartyPacket() : NetworkPacket {
    /** Whether this should be set as the player's party for rendering immediately. */
    var isThisPlayerParty: Boolean = false
    /** The UUID of the party storage. Does not need to be the player's UUID. */
    var uuid = UUID.randomUUID()
    /** The number of slots in the party. Defaults to 6. */
    var slots: Int = 6

    constructor(isThisPlayerParty: Boolean, uuid: UUID, slots: Int): this() {
        this.isThisPlayerParty = isThisPlayerParty
        this.uuid = uuid
        this.slots = slots
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeBoolean(isThisPlayerParty)
        buffer.writeUuid(uuid)
        buffer.writeSizedInt(IntSize.U_BYTE, slots)
    }

    override fun decode(buffer: PacketByteBuf) {
        isThisPlayerParty = buffer.readBoolean()
        uuid = buffer.readUuid()
        slots = buffer.readSizedInt(IntSize.U_BYTE)
    }
}