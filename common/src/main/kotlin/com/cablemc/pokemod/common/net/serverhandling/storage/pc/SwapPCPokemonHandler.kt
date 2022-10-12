/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.net.serverhandling.storage.pc

import com.cablemc.pokemod.common.PokemodNetwork.NetworkContext
import com.cablemc.pokemod.common.api.storage.pc.link.PCLinkManager
import com.cablemc.pokemod.common.net.messages.client.storage.pc.ClosePCPacket
import com.cablemc.pokemod.common.net.messages.server.storage.pc.SwapPCPokemonPacket
import com.cablemc.pokemod.common.net.serverhandling.ServerPacketHandler
import net.minecraft.server.network.ServerPlayerEntity

object SwapPCPokemonHandler : ServerPacketHandler<SwapPCPokemonPacket> {
    override fun invokeOnServer(packet: SwapPCPokemonPacket, ctx: NetworkContext, player: ServerPlayerEntity) {
        val pc = PCLinkManager.getPC(player) ?: return run { ClosePCPacket().sendToPlayer(player) }
        if (pc[packet.position1]?.uuid != packet.pokemon1ID || pc[packet.position2]?.uuid != packet.pokemon2ID) {
            return
        }
        pc.swap(packet.position1, packet.position2)
    }
}