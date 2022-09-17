/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.net.messages.server.storage.pc

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.api.storage.pc.PCPosition
import com.cablemc.pokemoncobbled.common.api.storage.pc.PCPosition.Companion.readPCPosition
import com.cablemc.pokemoncobbled.common.api.storage.pc.PCPosition.Companion.writePCPosition
import com.cablemc.pokemoncobbled.common.net.serverhandling.storage.pc.MovePCPokemonHandler
import net.minecraft.network.PacketByteBuf
import java.util.UUID

/**
 * Tells the server to move a PC Pokémon from one position of the player's currently linked PC to another.
 *
 * Handled by [MovePCPokemonHandler].
 *
 * @author Hiroku
 * @since June 20th, 2022
 */
class MovePCPokemonPacket() : NetworkPacket {
    lateinit var pokemonID: UUID
    lateinit var oldPosition: PCPosition
    lateinit var newPosition: PCPosition

    constructor(pokemonID: UUID, oldPosition: PCPosition, newPosition: PCPosition): this() {
        this.pokemonID = pokemonID
        this.oldPosition = oldPosition
        this.newPosition = newPosition
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUuid(pokemonID)
        buffer.writePCPosition(oldPosition)
        buffer.writePCPosition(newPosition)
    }

    override fun decode(buffer: PacketByteBuf) {
        pokemonID = buffer.readUuid()
        oldPosition = buffer.readPCPosition()
        newPosition = buffer.readPCPosition()
    }
}