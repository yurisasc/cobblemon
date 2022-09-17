/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.net.messages.server.storage.pc

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyPosition
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyPosition.Companion.readPartyPosition
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyPosition.Companion.writePartyPosition
import com.cablemc.pokemoncobbled.common.api.storage.pc.PCPosition
import com.cablemc.pokemoncobbled.common.api.storage.pc.PCPosition.Companion.readPCPosition
import com.cablemc.pokemoncobbled.common.api.storage.pc.PCPosition.Companion.writePCPosition
import com.cablemc.pokemoncobbled.common.net.serverhandling.storage.pc.MovePCPokemonToPartyHandler
import net.minecraft.network.PacketByteBuf
import java.util.UUID

/**
 * Tells the server to move a Pokémon from a player's linked PC to their party. If the party position is
 * not specified, it will attempt to put the Pokémon in the first available space.
 *
 * Handled by [MovePCPokemonToPartyHandler].
 *
 * @author Hiroku
 * @since June 20th, 2022
 */
class MovePCPokemonToPartyPacket() : NetworkPacket {
    lateinit var pokemonID: UUID
    lateinit var pcPosition: PCPosition
    var partyPosition: PartyPosition? = null

    constructor(pokemonID: UUID, pcPosition: PCPosition, partyPosition: PartyPosition?): this() {
        this.pokemonID = pokemonID
        this.pcPosition = pcPosition
        this.partyPosition = partyPosition
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUuid(pokemonID)
        buffer.writePCPosition(pcPosition)
        buffer.writeBoolean(partyPosition != null)
        partyPosition?.let { buffer.writePartyPosition(it) }
    }

    override fun decode(buffer: PacketByteBuf) {
        pokemonID = buffer.readUuid()
        pcPosition = buffer.readPCPosition()
        if (buffer.readBoolean()) {
            partyPosition = buffer.readPartyPosition()
        }
    }
}