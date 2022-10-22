/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.net.messages.client.storage.pc

import com.cablemc.pokemod.common.api.storage.pc.PCPosition
import com.cablemc.pokemod.common.api.storage.pc.PCPosition.Companion.readPCPosition
import com.cablemc.pokemod.common.api.storage.pc.PCPosition.Companion.writePCPosition
import com.cablemc.pokemod.common.net.messages.client.storage.SetPokemonPacket
import com.cablemc.pokemod.common.pokemon.Pokemon
import java.util.UUID
import net.minecraft.network.PacketByteBuf

/**
 * Sets a specific Pokémon in a specific slot of the client-side representation of a PC.
 *
 * Handled by [com.cablemc.pokemod.common.client.net.storage.pc.SetPCPokemonHandler].
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