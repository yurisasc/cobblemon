/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.fabric.net

import com.cobblemon.mod.common.CobblemonNetwork
import com.cobblemon.mod.common.NetworkManager
import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.network.Packet
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import kotlin.reflect.KClass

object CobblemonFabricNetworkManager : NetworkManager {

    override fun initClient() {
        CobblemonNetwork.initClient()
    }

    override fun initServer() {
        CobblemonNetwork.initServer()
    }

    override fun <T : NetworkPacket<T>> registerClientBound(
        identifier: Identifier,
        kClass: KClass<T>,
        encoder: (T, PacketByteBuf) -> Unit,
        decoder: (PacketByteBuf) -> T,
        handler: ClientNetworkPacketHandler<T>
    ) {
        ClientPlayNetworking.registerGlobalReceiver(identifier, this.createClientBoundHandler(decoder::invoke, handler::handle))
    }

    override fun <T : NetworkPacket<T>> registerServerBound(
        identifier: Identifier,
        kClass: KClass<T>,
        encoder: (T, PacketByteBuf) -> Unit,
        decoder: (PacketByteBuf) -> T,
        handler: ServerNetworkPacketHandler<T>
    ) {
        ServerPlayNetworking.registerGlobalReceiver(identifier, this.createServerBoundHandler(decoder::invoke, handler::handle))
    }

    private fun <T : NetworkPacket<*>> createServerBoundHandler(
        decoder: (PacketByteBuf) -> T,
        handler: (T, MinecraftServer, ServerPlayerEntity) -> Unit
    ) = ServerPlayNetworking.PlayChannelHandler { server, player, _, buffer, _ ->
        handler(decoder(buffer), server, player)
    }

    private fun <T : NetworkPacket<*>> createClientBoundHandler(
        decoder: (PacketByteBuf) -> T,
        handler: (T, MinecraftClient) -> Unit
    ) = ClientPlayNetworking.PlayChannelHandler { client, _,  buffer, _ ->
        handler(decoder(buffer), client)
    }

    override fun sendPacketToPlayer(player: ServerPlayerEntity, packet: NetworkPacket<*>) {
        ServerPlayNetworking.send(player, packet.id, packet.toBuffer())
    }

    override fun sendPacketToServer(packet: NetworkPacket<*>) {
        ClientPlayNetworking.send(packet.id, packet.toBuffer())
    }

    override fun <T : NetworkPacket<*>> asVanillaClientBound(packet: T): Packet<ClientPlayPacketListener> {
        return ServerPlayNetworking.createS2CPacket(packet.id, packet.toBuffer())
    }
}