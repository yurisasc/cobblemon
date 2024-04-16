/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.serverhandling

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.cobblemon.mod.common.battles.BattleRegistry
import com.cobblemon.mod.common.net.messages.client.PlayerInteractOptionsPacket
import com.cobblemon.mod.common.net.messages.server.RequestPlayerInteractionsPacket
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import java.util.EnumSet

object RequestInteractionsHandler : ServerNetworkPacketHandler<RequestPlayerInteractionsPacket> {
    override fun handle(
        packet: RequestPlayerInteractionsPacket,
        server: MinecraftServer,
        player: ServerPlayerEntity
    ) {
        //We could potentially check if the targeted player has pokemon here
        val options = EnumSet.of(PlayerInteractOptionsPacket.Options.TRADE)

        val isTargetBattling = BattleRegistry.getBattleByParticipatingPlayerId(packet.targetId) != null
        if (isTargetBattling and Cobblemon.config.allowSpectating) {
            options.add(PlayerInteractOptionsPacket.Options.SPECTATE_BATTLE)
        }
        else {
            options.add(PlayerInteractOptionsPacket.Options.BATTLE)
            if(BattleRegistry.playerToTeam[player.uuid] != null && BattleRegistry.playerToTeam[packet.targetId] !== null) {
                if(BattleRegistry.playerToTeam[player.uuid]?.teamID != BattleRegistry.playerToTeam[packet.targetId]?.teamID) {
                    options.add(PlayerInteractOptionsPacket.Options.MULTI_BATTLE)
                } else {
                    options.add(PlayerInteractOptionsPacket.Options.TEAM_LEAVE)
                }
            } else if(BattleRegistry.playerToTeam[player.uuid] === null && BattleRegistry.playerToTeam[packet.targetId] === null) {
                // TODO: Max team size checking, allow for team of size > 2
                options.add(PlayerInteractOptionsPacket.Options.TEAM_REQUEST)
            }
        }
        PlayerInteractOptionsPacket(options, packet.targetId, packet.targetNumericId, packet.pokemonId).sendToPlayer(player)

    }

}