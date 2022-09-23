/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.net.serverhandling.storage.pc

import com.cablemc.pokemoncobbled.common.CobbledNetwork.NetworkContext
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.storage.pc.link.PCLinkManager
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.pc.ClosePCPacket
import com.cablemc.pokemoncobbled.common.net.messages.server.storage.pc.MovePartyPokemonToPCPacket
import com.cablemc.pokemoncobbled.common.net.serverhandling.ServerPacketHandler
import net.minecraft.server.network.ServerPlayerEntity

object MovePartyPokemonToPCHandler : ServerPacketHandler<MovePartyPokemonToPCPacket> {
    override fun invokeOnServer(packet: MovePartyPokemonToPCPacket, ctx: NetworkContext, player: ServerPlayerEntity) {
        val party = PokemonCobbled.storage.getParty(player)
        val pc = PCLinkManager.getPC(player) ?: return run { ClosePCPacket().sendToPlayer(player) }

        val pokemon = party[packet.partyPosition] ?: return
        if (pokemon.uuid != packet.pokemonID) {
            return
        }

        val pcPosition = packet.pcPosition?.takeIf { pc[it] == null } ?: pc.getFirstAvailablePosition() ?: return
        party.remove(packet.partyPosition)
        pc[pcPosition] = pokemon
    }
}