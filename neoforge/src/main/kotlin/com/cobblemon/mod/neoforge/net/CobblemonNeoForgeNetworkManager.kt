/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.neoforge.net

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonNetwork
import com.cobblemon.mod.common.NetworkManager
import com.cobblemon.mod.common.api.net.NetworkPacket
import net.minecraft.client.MinecraftClient
import net.minecraft.server.network.ServerPlayerEntity
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent

// https://neoforged.net/news/20.4networking-rework/
object CobblemonNeoForgeNetworkManager : NetworkManager {
    const val PROTOCOL_VERSION = "1.0.0"

    fun registerMessages(event: RegisterPayloadHandlersEvent) {
        val registrar = event
            .registrar(Cobblemon.MODID)
            .versioned(PROTOCOL_VERSION)
        CobblemonNetwork.s2cPayloads.map { NeoForgePacketInfo(it) }.forEach { it.registerToClient(registrar) }
        CobblemonNetwork.c2sPayloads.map { NeoForgePacketInfo(it) }.forEach { it.registerToServer(registrar) }
    }

    override fun sendPacketToPlayer(player: ServerPlayerEntity, packet: NetworkPacket<*>) {
        player.networkHandler.send(packet)
    }

    override fun sendToServer(packet: NetworkPacket<*>) {
        MinecraftClient.getInstance().networkHandler?.send(packet)
    }
}