/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.serverhandling.battle

import com.cobblemon.mod.common.CobblemonNetwork
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.battles.BattleRegistry
import com.cobblemon.mod.common.exception.IllegalActionChoiceException
import com.cobblemon.mod.common.net.PacketHandler
import com.cobblemon.mod.common.net.messages.client.battle.BattleMakeChoicePacket
import com.cobblemon.mod.common.net.messages.client.battle.BattleQueueRequestPacket
import com.cobblemon.mod.common.net.messages.server.battle.BattleSelectActionsPacket
import com.cobblemon.mod.common.util.runOnServer

object BattleSelectActionsHandler : PacketHandler<BattleSelectActionsPacket> {
    override fun invoke(packet: BattleSelectActionsPacket, ctx: CobblemonNetwork.NetworkContext) {
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