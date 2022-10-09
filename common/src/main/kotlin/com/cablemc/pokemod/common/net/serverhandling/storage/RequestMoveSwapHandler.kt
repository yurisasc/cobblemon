/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.net.serverhandling.storage

import com.cablemc.pokemod.common.Pokemod
import com.cablemc.pokemod.common.PokemodNetwork
import com.cablemc.pokemod.common.net.messages.server.RequestMoveSwapPacket
import com.cablemc.pokemod.common.net.serverhandling.ServerPacketHandler
import net.minecraft.server.network.ServerPlayerEntity

object RequestMoveSwapHandler: ServerPacketHandler<RequestMoveSwapPacket> {
    override fun invokeOnServer(packet: RequestMoveSwapPacket, ctx: PokemodNetwork.NetworkContext, player: ServerPlayerEntity) {
        val pokemon = Pokemod.storage.getParty(player).get(packet.slot) ?: return
        val move1 = pokemon.moveSet[packet.move1] ?: return
        val move2 = pokemon.moveSet[packet.move2] ?: return
        if (move1 == move2) {
            return
        }
        pokemon.moveSet.swapMove(packet.move1, packet.move2)
    }
}