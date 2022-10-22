/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.net.serverhandling.storage.pc

import com.cablemc.pokemod.common.Pokemod
import com.cablemc.pokemod.common.PokemodNetwork.NetworkContext
import com.cablemc.pokemod.common.api.storage.pc.link.PCLinkManager
import com.cablemc.pokemod.common.net.messages.client.storage.pc.ClosePCPacket
import com.cablemc.pokemod.common.net.messages.server.storage.pc.MovePCPokemonToPartyPacket
import com.cablemc.pokemod.common.net.serverhandling.ServerPacketHandler
import net.minecraft.server.network.ServerPlayerEntity

object MovePCPokemonToPartyHandler : ServerPacketHandler<MovePCPokemonToPartyPacket> {
    override fun invokeOnServer(packet: MovePCPokemonToPartyPacket, ctx: NetworkContext, player: ServerPlayerEntity) {
        val party = Pokemod.storage.getParty(player)
        val pc = PCLinkManager.getPC(player) ?: return run { ClosePCPacket().sendToPlayer(player) }

        val pokemon = pc[packet.pcPosition] ?: return
        if (pokemon.uuid != packet.pokemonID) {
            return
        }

        val partyPosition = packet.partyPosition?.takeIf { party[it] == null } ?: party.getFirstAvailablePosition() ?: return
        pc.remove(packet.pcPosition)
        party[partyPosition] = pokemon
    }
}