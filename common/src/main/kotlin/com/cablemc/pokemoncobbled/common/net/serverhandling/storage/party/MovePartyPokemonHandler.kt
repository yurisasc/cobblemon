/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.net.serverhandling.storage.party

import com.cablemc.pokemoncobbled.common.CobbledNetwork.NetworkContext
import com.cablemc.pokemoncobbled.common.net.messages.server.storage.party.MovePartyPokemonPacket
import com.cablemc.pokemoncobbled.common.net.serverhandling.ServerPacketHandler
import com.cablemc.pokemoncobbled.common.util.party
import net.minecraft.server.network.ServerPlayerEntity

object MovePartyPokemonHandler : ServerPacketHandler<MovePartyPokemonPacket> {
    override fun invokeOnServer(packet: MovePartyPokemonPacket, ctx: NetworkContext, player: ServerPlayerEntity) {
        val party = player.party()
        val pokemon = party[packet.oldPosition] ?: return
        if (pokemon.uuid != packet.pokemonID) {
            return
        }
        if (party[packet.newPosition] != null) {
            // Should've been a swap, something is funky on the client
            return
        }
        party.move(pokemon, packet.newPosition)
    }
}