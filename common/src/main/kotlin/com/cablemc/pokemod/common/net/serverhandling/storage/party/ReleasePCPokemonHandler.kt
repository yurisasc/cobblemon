/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.net.serverhandling.storage.party

import com.cablemc.pokemod.common.PokemodNetwork
import com.cablemc.pokemod.common.api.storage.pc.link.PCLinkManager
import com.cablemc.pokemod.common.net.messages.server.storage.pc.ReleasePCPokemonPacket
import com.cablemc.pokemod.common.net.serverhandling.ServerPacketHandler
import net.minecraft.server.network.ServerPlayerEntity

object ReleasePCPokemonHandler : ServerPacketHandler<ReleasePCPokemonPacket> {
    override fun invokeOnServer(packet: ReleasePCPokemonPacket, ctx: PokemodNetwork.NetworkContext, player: ServerPlayerEntity) {
        val pc = PCLinkManager.getPC(player) ?: return
        val pokemon = pc[packet.position] ?: return
        if (pokemon.uuid != packet.pokemonID) {
            return // Not what we expected
        }

        pc.remove(packet.position)
    }
}