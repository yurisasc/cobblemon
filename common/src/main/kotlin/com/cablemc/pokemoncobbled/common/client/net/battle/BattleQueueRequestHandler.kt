/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.client.net.battle

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.battle.SingleActionRequest
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.battle.BattleQueueRequestPacket
import net.minecraft.client.MinecraftClient

object BattleQueueRequestHandler : ClientPacketHandler<BattleQueueRequestPacket> {
    override fun invokeOnClient(packet: BattleQueueRequestPacket, ctx: CobbledNetwork.NetworkContext) {
        val battle = PokemonCobbledClient.battle ?: return
        val actor = battle.side1.actors.find { it.uuid == MinecraftClient.getInstance().player?.uuid } ?: return
        PokemonCobbledClient.battle?.pendingActionRequests = SingleActionRequest.composeFrom(actor, packet.request)
    }
}