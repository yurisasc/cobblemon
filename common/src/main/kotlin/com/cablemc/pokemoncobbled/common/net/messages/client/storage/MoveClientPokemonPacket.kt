/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.net.messages.client.storage

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.api.storage.StorePosition
import net.minecraft.network.PacketByteBuf
import java.util.UUID

/**
 * Base packet class for moving a Pokémon from one position to another in the same store.
 *
 * @author Hiroku
 * @since June 18th, 2022
 */
abstract class MoveClientPokemonPacket<T : StorePosition> : NetworkPacket {
    lateinit var storeID: UUID
    lateinit var pokemonID: UUID
    lateinit var newPosition: T

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUuid(storeID)
        buffer.writeUuid(pokemonID)
        encodePosition(buffer, newPosition)
    }

    override fun decode(buffer: PacketByteBuf) {
        storeID = buffer.readUuid()
        pokemonID = buffer.readUuid()
        newPosition = decodePosition(buffer)
    }

    abstract fun encodePosition(buffer: PacketByteBuf, position: T)
    abstract fun decodePosition(buffer: PacketByteBuf): T
}