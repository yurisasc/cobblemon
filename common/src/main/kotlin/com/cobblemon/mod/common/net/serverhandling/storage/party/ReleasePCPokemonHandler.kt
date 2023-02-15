/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.serverhandling.storage.party

import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.cobblemon.mod.common.api.storage.pc.link.PCLinkManager
import com.cobblemon.mod.common.net.messages.server.storage.pc.ReleasePCPokemonPacket
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity

object ReleasePCPokemonHandler : ServerNetworkPacketHandler<ReleasePCPokemonPacket> {
    override fun handle(packet: ReleasePCPokemonPacket, server: MinecraftServer, player: ServerPlayerEntity) {
        val pc = PCLinkManager.getPC(player) ?: return
        val pokemon = pc[packet.position] ?: return
        if (pokemon.uuid != packet.pokemonID) {
            return // Not what we expected
        }

        pc.remove(packet.position)
    }
}