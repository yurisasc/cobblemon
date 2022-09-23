/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.net.messages.client.storage.party

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.api.storage.PokemonStore
import net.minecraft.network.PacketByteBuf
import java.util.UUID

/**
 * Sets the given party store as the player's active party. This will change what the overlay
 * is showing, but will fail if the store supplied in this packet is one unknown to the client.
 * To inform the player about a store before doing this, [PokemonStore.sendTo] will serve this
 * purpose. If you have previously used [InitializePartyPacket] then that won't be necessary.
 *
 * Handled by [com.cablemc.pokemoncobbled.common.client.net.storage.party.SetPartyReferenceHandler]
 *
 * @author Hiroku
 * @since November 29th, 2021
 */
class SetPartyReferencePacket() : NetworkPacket {
    lateinit var storeID: UUID

    constructor(storageID: UUID): this() {
        this.storeID = storageID
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUuid(this.storeID)
    }

    override fun decode(buffer: PacketByteBuf) {
        this.storeID = buffer.readUuid()
    }
}