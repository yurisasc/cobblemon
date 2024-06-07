/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.fabric.net

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonNetwork
import com.cobblemon.mod.common.NetworkManager
import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import kotlin.reflect.KClass
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.Packet
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.CustomPayload
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

object CobblemonFabricNetworkManager : NetworkManager {


    override fun <T : NetworkPacket<T>> createServerBoundPayload(
        identifier: Identifier,
        kClass: KClass<T>,
        encoder: (T, RegistryByteBuf) -> Unit,
        decoder: (RegistryByteBuf) -> T,
        handler: ServerNetworkPacketHandler<T>
    ) {
        PayloadTypeRegistry.playS2C().register(CustomPayload.id(identifier.toString()), PacketCodec.of(encoder, decoder))
    }

    override fun <T : NetworkPacket<T>> createServerBoundHandler(
        identifier: Identifier,
        handler: (T, MinecraftServer, ServerPlayerEntity) -> Unit
    ) {
        ServerPlayNetworking.registerGlobalReceiver(CustomPayload.id(identifier.toString())) { obj, context ->
            val newObj = obj as? T
            if (newObj == null) {
                Cobblemon.LOGGER.error("Somehow the packet we're handling didnt match the type expected?")
                return@registerGlobalReceiver
            }
            handler.invoke(newObj, context.player().server, context.player())
        }
    }

    override fun <T : NetworkPacket<T>> createClientBoundPayload(
        identifier: Identifier,
        kClass: KClass<T>,
        encoder: (T, RegistryByteBuf) -> Unit,
        decoder: (RegistryByteBuf) -> T,
        handler: ClientNetworkPacketHandler<T>
    ) {
        PayloadTypeRegistry.playS2C().register(CustomPayload.id(identifier.toString()), PacketCodec.of(encoder, decoder))
    }

    override fun <T : NetworkPacket<T>> createClientBoundHandler(
        identifier: Identifier,
        handler: (T, MinecraftClient) -> Unit
    ) {
        ClientPlayNetworking.registerGlobalReceiver(CustomPayload.id(identifier.toString())) { obj, context ->
            val newObj = obj as? T
            if (newObj == null) {
                Cobblemon.LOGGER.error("Somehow the packet we're handling didnt match the type expected?")
                return@registerGlobalReceiver
            }
            handler.invoke(newObj, MinecraftClient.getInstance())
        }
    }

    override fun sendPacketToPlayer(player: ServerPlayerEntity, packet: NetworkPacket<*>) {
        ServerPlayNetworking.send(player, packet)
    }

    override fun sendPacketToServer(packet: NetworkPacket<*>) {
        ClientPlayNetworking.send(packet)
    }
}