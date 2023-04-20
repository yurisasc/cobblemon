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
import java.util.UUID
import net.minecraft.network.PacketByteBuf

/**
 * Packet sent to the server by a player that wants to reset the pastured Pokémon, meaning attempt spawning the
 * Pokémon again in case the entity is missing.
 *
 * @author Hiroku
 * @since April 16th, 2023
 *
 * TODO PASTURE packet handler
 */
class ResetPasturePokemonPacket(val pastureId: UUID, val pokemonId: UUID) : NetworkPacket<ResetPasturePokemonPacket> {
    companion object {
        val ID = cobblemonResource("reset_pasture_pokemon")
        fun decode(buffer: PacketByteBuf) = ResetPasturePokemonPacket(buffer.readUuid(), buffer.readUuid())
    }

    override val id = ID
    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUuid(pastureId)
        buffer.writeUuid(pokemonId)
    }
}