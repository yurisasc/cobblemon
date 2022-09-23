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
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.network.PacketByteBuf
import java.util.UUID

/**
 * Base packet class for creating a new Pokémon in one of the client's stores. This
 * is for when the Pokémon is currently unrecognized by that specific store on the
 * client. Implementations target specific store types.
 *
 * @author Hiroku
 * @since November 29th, 2021
 */
abstract class SetPokemonPacket<T : StorePosition> : NetworkPacket {
    var pokemon = Pokemon()
    var storeID = UUID.randomUUID()
    lateinit var storePosition: T

    abstract fun encodePosition(buffer: PacketByteBuf)
    override fun encode(buffer: PacketByteBuf) {
        pokemon.saveToBuffer(buffer, toClient = true)
        buffer.writeUuid(storeID)
        encodePosition(buffer)
    }

    abstract fun decodePosition(buffer: PacketByteBuf): T
    override fun decode(buffer: PacketByteBuf) {
        pokemon.loadFromBuffer(buffer)
        storeID = buffer.readUuid()
        storePosition = decodePosition(buffer)
    }
}