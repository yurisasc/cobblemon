/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.net.serverhandling.storage

import com.cablemc.pokemod.common.Pokemod
import com.cablemc.pokemod.common.PokemodNetwork.NetworkContext
import com.cablemc.pokemod.common.api.storage.pc.link.PCLinkManager
import com.cablemc.pokemod.common.net.messages.client.storage.pc.ClosePCPacket
import com.cablemc.pokemod.common.net.messages.server.storage.SwapPCPartyPokemonPacket
import com.cablemc.pokemod.common.net.serverhandling.ServerPacketHandler
import net.minecraft.server.network.ServerPlayerEntity

object SwapPCPartyPokemonHandler : ServerPacketHandler<SwapPCPartyPokemonPacket> {
    override fun invokeOnServer(packet: SwapPCPartyPokemonPacket, ctx: NetworkContext, player: ServerPlayerEntity) {
        val party = Pokemod.storage.getParty(player)
        val pc = PCLinkManager.getPC(player) ?: return run { ClosePCPacket().sendToPlayer(player) }

        val partyPokemon = party[packet.partyPosition] ?: return
        val pcPokemon = pc[packet.pcPosition] ?: return

        if (partyPokemon.uuid != packet.partyPokemonID || pcPokemon.uuid != packet.pcPokemonID) {
            return
        }

        party[packet.partyPosition] = pcPokemon
        pc[packet.pcPosition] = partyPokemon
    }
}