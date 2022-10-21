/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.net.serverhandling.storage.party

import com.cablemc.pokemod.common.PokemodNetwork.NetworkContext
import com.cablemc.pokemod.common.net.messages.server.storage.party.SwapPartyPokemonPacket
import com.cablemc.pokemod.common.net.serverhandling.ServerPacketHandler
import com.cablemc.pokemod.common.util.party
import net.minecraft.server.network.ServerPlayerEntity

object SwapPartyPokemonHandler : ServerPacketHandler<SwapPartyPokemonPacket> {
    override fun invokeOnServer(packet: SwapPartyPokemonPacket, ctx: NetworkContext, player: ServerPlayerEntity) {
        val party = player.party()

        val pokemon1 = party[packet.position1] ?: return
        val pokemon2 = party[packet.position2] ?: return

        if (pokemon1.uuid != packet.pokemon1ID || pokemon2.uuid != packet.pokemon2ID || packet.position1 == packet.position2) {
            return
        }

        party.swap(packet.position1, packet.position2)
    }
}