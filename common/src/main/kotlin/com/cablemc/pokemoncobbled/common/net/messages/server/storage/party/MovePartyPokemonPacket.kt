/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.net.messages.server.storage.party

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyPosition
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyPosition.Companion.readPartyPosition
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyPosition.Companion.writePartyPosition
import com.cablemc.pokemoncobbled.common.net.serverhandling.storage.party.MovePartyPokemonHandler
import net.minecraft.network.PacketByteBuf
import java.util.UUID

/**
 * Tells the server to move a party Pokémon from one position of the player's party to another.
 *
 * Handled by [MovePartyPokemonHandler].
 *
 * @author Hiroku
 * @since June 20th, 2022
 */
class MovePartyPokemonPacket() : NetworkPacket {
    lateinit var pokemonID: UUID
    lateinit var oldPosition: PartyPosition
    lateinit var newPosition: PartyPosition

    constructor(pokemonID: UUID, oldPosition: PartyPosition, newPosition: PartyPosition): this() {
        this.pokemonID = pokemonID
        this.oldPosition = oldPosition
        this.newPosition = newPosition
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUuid(pokemonID)
        buffer.writePartyPosition(oldPosition)
        buffer.writePartyPosition(newPosition)
    }

    override fun decode(buffer: PacketByteBuf) {
        pokemonID = buffer.readUuid()
        oldPosition = buffer.readPartyPosition()
        newPosition = buffer.readPartyPosition()
    }
}