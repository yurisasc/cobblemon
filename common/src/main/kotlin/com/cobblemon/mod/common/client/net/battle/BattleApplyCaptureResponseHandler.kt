/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.net.battle

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.cobblemon.mod.common.battles.BallActionResponse
import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.client.gui.battle.BattleGUI
import com.cobblemon.mod.common.net.messages.client.battle.BattleApplyCaptureResponsePacket
import net.minecraft.client.MinecraftClient

object BattleApplyCaptureResponseHandler : ClientNetworkPacketHandler<BattleApplyCaptureResponsePacket> {
    override fun handle(packet: BattleApplyCaptureResponsePacket, client: MinecraftClient) {
        val battle = CobblemonClient.battle ?: return
        val req = battle.pendingActionRequests.firstOrNull { it.response == null } ?: return
        val res = BallActionResponse()
        val gui = MinecraftClient.getInstance().currentScreen
        if (gui is BattleGUI) {
            gui.selectAction(req, res)
        } else {
            req.response = res
            battle.checkForFinishedChoosing()
        }
    }
}