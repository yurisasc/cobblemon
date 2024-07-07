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
import com.cobblemon.mod.common.util.traceFirstEntityCollision
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.ClipContext
import java.util.EnumSet

object RequestInteractionsHandler : ServerNetworkPacketHandler<RequestPlayerInteractionsPacket> {

    const val MAX_PVE_WILD_DISTANCE = 12
    const val MAX_PVE_WILD_DISTANCE_SQ = MAX_PVE_WILD_DISTANCE * MAX_PVE_WILD_DISTANCE
    const val MAX_TRADE_DISTANCE = 12
    const val MAX_TRADE_DISTANCE_SQ = MAX_TRADE_DISTANCE * MAX_TRADE_DISTANCE
    const val MAX_PVP_DISTANCE = 32
    const val MAX_PVP_DISTANCE_SQ = MAX_PVP_DISTANCE * MAX_PVP_DISTANCE
    const val MAX_SPECTATE_DISTANCE = 64
    const val MAX_SPECTATE_DISTANCE_SQ = MAX_SPECTATE_DISTANCE * MAX_SPECTATE_DISTANCE
    const val MAX_ENTITY_INTERACTION_DISTANCE = 64

    override fun handle(
        packet: RequestPlayerInteractionsPacket,
        server: MinecraftServer,
        player: ServerPlayer
    ) {
        val world = player.level()
        val targetPlayerEntity = world.getPlayerByUUID(packet.targetId)
        val options = EnumSet.noneOf(PlayerInteractOptionsPacket.Options::class.java)
        if (targetPlayerEntity != null && player.traceFirstEntityCollision(
            entityClass = LivingEntity::class.java,
            ignoreEntity = player,
            maxDistance = MAX_ENTITY_INTERACTION_DISTANCE.toFloat(),
            collideBlock = ClipContext.Fluid.NONE
        ) == targetPlayerEntity) {
            //We could potentially check if the targeted player has pokemon here
            val squaredDistance = targetPlayerEntity.position().distanceToSqr(player.position())
            if(squaredDistance <= MAX_TRADE_DISTANCE_SQ) {
                options.add(PlayerInteractOptionsPacket.Options.TRADE)
            }

            val isTargetBattling = BattleRegistry.getBattleByParticipatingPlayerId(packet.targetId) != null
            if (isTargetBattling and Cobblemon.config.allowSpectating && squaredDistance <= MAX_SPECTATE_DISTANCE_SQ) {
                options.add(PlayerInteractOptionsPacket.Options.SPECTATE_BATTLE)
            }
            else if (squaredDistance <= MAX_PVP_DISTANCE_SQ) {
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
        }
        if (!options.isEmpty()) {
            PlayerInteractOptionsPacket(options, packet.targetId, packet.targetNumericId, packet.pokemonId).sendToPlayer(player)
        }
    }

}