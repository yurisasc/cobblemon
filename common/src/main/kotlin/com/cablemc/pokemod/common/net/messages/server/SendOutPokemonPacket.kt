/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.net.messages.server

import com.cablemc.pokemod.common.api.net.NetworkPacket
import com.cablemc.pokemod.common.net.IntSize
import com.cablemc.pokemod.common.net.serverhandling.storage.SendOutPokemonHandler
import com.cablemc.pokemod.common.util.readSizedInt
import com.cablemc.pokemod.common.util.writeSizedInt
import net.minecraft.network.PacketByteBuf

/**
 * Packet sent from the client to the server to send out the Pokémon in the specified
 * slot.
 *
 * Handled by [SendOutPokemonHandler]
 *
 * @author Hiroku
 * @since December 2nd, 2021
 */
class SendOutPokemonPacket() : NetworkPacket {
    var slot = -1

    constructor(slot: Int): this() {
        this.slot = slot
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeSizedInt(IntSize.U_BYTE, slot)
    }

    override fun decode(buffer: PacketByteBuf) {
        slot = buffer.readSizedInt(IntSize.U_BYTE)
    }
}