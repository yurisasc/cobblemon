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
import com.cablemc.pokemoncobbled.common.net.serverhandling.storage.pc.MovePartyPokemonToPCHandler
import net.minecraft.network.PacketByteBuf
import java.util.UUID

/**
 * Tells the server to move a Pokémon from a player's party to their linked PC. If the PC position is
 * not specified, it will attempt to put the Pokémon in the first available space.
 *
 * Handled by [MovePartyPokemonToPCHandler].
 *
 * @author Hiroku
 * @since June 20th, 2022
 */
class MovePartyPokemonToPCPacket() : NetworkPacket {
    lateinit var pokemonID: UUID
    lateinit var partyPosition: PartyPosition
    var pcPosition: PCPosition? = null

    constructor(pokemonID: UUID, partyPosition: PartyPosition, pcPosition: PCPosition?): this() {
        this.pokemonID = pokemonID
        this.partyPosition = partyPosition
        this.pcPosition = pcPosition
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUuid(pokemonID)
        buffer.writePartyPosition(partyPosition)
        buffer.writeBoolean(pcPosition != null)
        pcPosition?.let { buffer.writePCPosition(it) }
    }

    override fun decode(buffer: PacketByteBuf) {
        pokemonID = buffer.readUuid()
        partyPosition = buffer.readPartyPosition()
        if (buffer.readBoolean()) {
            pcPosition = buffer.readPCPosition()
        }
    }
}