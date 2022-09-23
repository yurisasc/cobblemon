/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.client.net.battle

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.battles.BallActionResponse
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.gui.battle.BattleGUI
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.battle.BattleApplyCaptureResponsePacket
import net.minecraft.client.MinecraftClient

object BattleApplyCaptureResponseHandler : ClientPacketHandler<BattleApplyCaptureResponsePacket> {
    override fun invokeOnClient(packet: BattleApplyCaptureResponsePacket, ctx: CobbledNetwork.NetworkContext) {
        val battle = PokemonCobbledClient.battle ?: return
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