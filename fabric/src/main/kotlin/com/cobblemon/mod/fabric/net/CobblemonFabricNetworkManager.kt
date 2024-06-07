/*
 * Copyright (C) 2023 Cobblemon Contributors
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
import io.netty.buffer.ByteBuf
import kotlin.reflect.KClass
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.Packet
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.CustomPayload
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

object CobblemonFabricNetworkManager : NetworkManager {

    override fun registerClientBound() {
        CobblemonNetwork.registerClientBound()
    }

    override fun registerServerBound() {
        CobblemonNetwork.registerServerBound()
    }

    override fun <T : NetworkPacket<T>> createClientBound(
        identifier: Identifier,
        kClass: KClass<T>,
        encoder: (T, PacketByteBuf) -> Unit,
        decoder: (PacketByteBuf) -> T,
        handler: ClientNetworkPacketHandler<T>
    ) {
        val id = CustomPayload.Id<T>(identifier)
        PayloadTypeRegistry.playS2C().register(id, PacketCodec.of(encoder, decoder))
        ClientPlayNetworking.registerGlobalReceiver(id) { msg, context ->
            handler.handle(msg, context.client())
        }
    }

    override fun <T : NetworkPacket<T>> createServerBound(
        identifier: Identifier,
        kClass: KClass<T>,
        encoder: (T, PacketByteBuf) -> Unit,
        decoder: (PacketByteBuf) -> T,
        handler: ServerNetworkPacketHandler<T>
    ) {
        val id = CustomPayload.Id<T>(identifier)
        PayloadTypeRegistry.playC2S().register(id, PacketCodec.of(encoder, decoder))
        ServerPlayNetworking.registerGlobalReceiver(id) { msg, context ->
            handler.handle(msg, context.player().server, context.player())
        }
    }

    private fun <T : NetworkPacket<*>> createServerBoundHandler(
        handler: (T, MinecraftServer, ServerPlayerEntity) -> Unit
    ) = ServerPlayNetworking.PlayPayloadHandler<T> { payload, context ->
        handler(payload, context.player().server, context.player())
    }

    private fun <T : NetworkPacket<*>> createClientBoundHandler(
        handler: (T, MinecraftClient) -> Unit
    ) = ClientPlayNetworking.PlayPayloadHandler<T> { payload, context ->
        handler(payload, context.client())
    }

    override fun sendPacketToPlayer(player: ServerPlayerEntity, packet: NetworkPacket<*>) {
        ServerPlayNetworking.send(player, packet)
    }

    override fun sendPacketToServer(packet: NetworkPacket<*>) {
        ClientPlayNetworking.send(packet)
    }
}