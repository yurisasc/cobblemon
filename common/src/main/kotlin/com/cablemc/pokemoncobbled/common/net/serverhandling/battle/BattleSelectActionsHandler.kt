/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.net.serverhandling.battle

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.api.text.red
import com.cablemc.pokemoncobbled.common.battles.BattleRegistry
import com.cablemc.pokemoncobbled.common.exception.IllegalActionChoiceException
import com.cablemc.pokemoncobbled.common.net.PacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.battle.BattleMakeChoicePacket
import com.cablemc.pokemoncobbled.common.net.messages.client.battle.BattleQueueRequestPacket
import com.cablemc.pokemoncobbled.common.net.messages.server.battle.BattleSelectActionsPacket
import com.cablemc.pokemoncobbled.common.util.runOnServer

object BattleSelectActionsHandler : PacketHandler<BattleSelectActionsPacket> {
    override fun invoke(packet: BattleSelectActionsPacket, ctx: CobbledNetwork.NetworkContext) {
        runOnServer {
            val battle = BattleRegistry.getBattle(packet.battleId) ?: return@runOnServer
            val player = ctx.player ?: return@runOnServer
            val actor = battle.actors.find { player.uuid in it.getPlayerUUIDs() } ?: return@runOnServer

            if (!actor.mustChoose) {
                return@runOnServer
            }

            try {
                actor.setActionResponses(packet.showdownActionResponses)
            } catch (e: IllegalActionChoiceException) {
                player.sendMessage(e.message!!.red())
                actor.sendUpdate(BattleQueueRequestPacket(actor.request!!))
                actor.sendUpdate(BattleMakeChoicePacket())
            }
        }
    }
}