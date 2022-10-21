/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.net.battle

import com.cablemc.pokemod.common.PokemodNetwork
import com.cablemc.pokemod.common.battles.BallActionResponse
import com.cablemc.pokemod.common.client.PokemodClient
import com.cablemc.pokemod.common.client.gui.battle.BattleGUI
import com.cablemc.pokemod.common.client.net.ClientPacketHandler
import com.cablemc.pokemod.common.net.messages.client.battle.BattleApplyCaptureResponsePacket
import net.minecraft.client.MinecraftClient

object BattleApplyCaptureResponseHandler : ClientPacketHandler<BattleApplyCaptureResponsePacket> {
    override fun invokeOnClient(packet: BattleApplyCaptureResponsePacket, ctx: PokemodNetwork.NetworkContext) {
        val battle = PokemodClient.battle ?: return
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