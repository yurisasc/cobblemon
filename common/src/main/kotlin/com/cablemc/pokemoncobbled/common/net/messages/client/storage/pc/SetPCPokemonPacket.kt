/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.net.messages.client.storage.pc

import com.cablemc.pokemoncobbled.common.api.storage.pc.PCPosition
import com.cablemc.pokemoncobbled.common.api.storage.pc.PCPosition.Companion.readPCPosition
import com.cablemc.pokemoncobbled.common.api.storage.pc.PCPosition.Companion.writePCPosition
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.SetPokemonPacket
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.network.PacketByteBuf
import java.util.UUID

/**
 * Sets a specific Pokémon in a specific slot of the client-side representation of a PC.
 *
 * Handled by [com.cablemc.pokemoncobbled.common.client.net.storage.pc.SetPCPokemonHandler].
 *
 * @author Hiroku
 * @since June 18th, 2022
 */
class SetPCPokemonPacket() : SetPokemonPacket<PCPosition>() {
    constructor(storeID: UUID, storePosition: PCPosition, pokemon: Pokemon): this() {
        this.storeID = storeID
        this.storePosition = storePosition
        this.pokemon = pokemon
    }

    override fun encodePosition(buffer: PacketByteBuf) = buffer.writePCPosition(storePosition)
    override fun decodePosition(buffer: PacketByteBuf) = buffer.readPCPosition()
}