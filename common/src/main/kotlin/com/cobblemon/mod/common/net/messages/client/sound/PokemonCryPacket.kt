/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.sound

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf

/**
 * Packet sent to the client to indicate the specified Pokémon is doing its cry. This is intended
 * to find and play a cry animation (which should have the cry sound effect linked)
 *
 * Handled by [com.cobblemon.mod.common.client.net.sound.PokemonCryHandler].
 *
 * @author Hiroku
 * @since July 17th, 2023
 */
class PokemonCryPacket(val entityId: Int) : NetworkPacket<PokemonCryPacket> {
    companion object {
        val ID = cobblemonResource("pokemon_cry")

        fun decode(buffer: PacketByteBuf) = PokemonCryPacket(buffer.readInt())
    }

    override val id = ID
    override fun encode(buffer: PacketByteBuf) {
        buffer.writeInt(entityId)
    }
}