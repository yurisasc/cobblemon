/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.net.messages.client.storage.pc

import com.cablemc.pokemod.common.api.storage.pc.PCPosition
import com.cablemc.pokemod.common.api.storage.pc.PCPosition.Companion.readPCPosition
import com.cablemc.pokemod.common.api.storage.pc.PCPosition.Companion.writePCPosition
import com.cablemc.pokemod.common.net.messages.client.storage.MoveClientPokemonPacket
import java.util.UUID
import net.minecraft.network.PacketByteBuf

/**
 * Moves a Pokémon from one part of a PC to another on the client side.
 *
 * Handled by [com.cablemc.pokemod.common.client.net.storage.pc.MoveClientPCPokemonHandler].
 *
 * @author Hiroku
 * @since June 18th, 2022
 */
class MoveClientPCPokemonPacket() : MoveClientPokemonPacket<PCPosition>() {
    constructor(storeID: UUID, pokemonID: UUID, newPosition: PCPosition) : this() {
        this.storeID = storeID
        this.pokemonID = pokemonID
        this.newPosition = newPosition
    }

    override fun encodePosition(buffer: PacketByteBuf, position: PCPosition) = buffer.writePCPosition(position)
    override fun decodePosition(buffer: PacketByteBuf) = buffer.readPCPosition()
}