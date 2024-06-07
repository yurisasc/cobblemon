/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.server.pasture

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.readUuid
import com.cobblemon.mod.common.util.writeUuid
import io.netty.buffer.ByteBuf
import java.util.UUID

/**
 * Packet sent to the server to indicate that all pastured Pokémon should be removed.
 *
 * @author Hiroku
 * @since July 2nd, 2023
 */
class UnpastureAllPokemonPacket(val pastureId: UUID) : NetworkPacket<UnpastureAllPokemonPacket> {
    companion object {
        val ID = cobblemonResource("unpasture_all_pokemon")
        fun decode(buffer: ByteBuf) = UnpastureAllPokemonPacket(buffer.readUuid())
    }

    override val id = ID
    override fun encode(buffer: ByteBuf) {
        buffer.writeUuid(pastureId)
    }
}