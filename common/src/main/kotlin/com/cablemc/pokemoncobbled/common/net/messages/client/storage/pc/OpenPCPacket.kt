/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.net.messages.client.storage.pc

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.api.storage.pc.link.PCLink
import net.minecraft.network.PacketByteBuf
import java.util.UUID

/**
 * Notifies a player that they must open the PC GUI for the given PC store ID. This is assuming
 * that a [PCLink] has been created that will allow them to make edits to it.
 *
 * A possible future improvement to this would be having a readOnly boolean field which will lock
 * modifying actions on the client for read-only presentation of PCs.
 *
 * Handled by [com.cablemc.pokemoncobbled.common.client.net.storage.pc.OpenPCHandler]
 *
 * @author Hiroku
 * @since June 20th, 2022
 */
class OpenPCPacket() : NetworkPacket {
    lateinit var storeID: UUID

    constructor(storeID: UUID): this() {
        this.storeID = storeID
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUuid(storeID)
    }

    override fun decode(buffer: PacketByteBuf) {
        storeID = buffer.readUuid()
    }
}