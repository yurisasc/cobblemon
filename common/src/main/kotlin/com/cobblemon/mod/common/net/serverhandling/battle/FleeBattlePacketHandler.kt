/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.serverhandling.battle

import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.cobblemon.mod.common.battles.BattleRegistry
import com.cobblemon.mod.common.net.messages.server.battle.FleeBattlePacket
import com.cobblemon.mod.common.util.battleLang
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity

object FleeBattlePacketHandler : ServerNetworkPacketHandler<FleeBattlePacket> {

    override fun handle(packet: FleeBattlePacket, server: MinecraftServer, player: ServerPlayerEntity) {
        val battle = BattleRegistry.getBattle(packet.battleId) ?: return
        player.sendMessage(battleLang("flee"))
        battle.end()
    }
}
