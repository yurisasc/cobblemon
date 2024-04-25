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
import com.cobblemon.mod.common.CobblemonNetwork.sendPacket
import com.cobblemon.mod.common.NetworkManager
import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.MinecraftClient
import kotlin.reflect.KClass
import net.minecraft.network.packet.Packet
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.CustomPayload
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket
import net.minecraft.server.MinecraftServer
import net.minecraft.server.dedicated.MinecraftDedicatedServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.world.Difficulty
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.handling.IPayloadHandler
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import java.util.function.BiConsumer

//https://neoforged.net/news/20.4networking-rework/
object CobblemonNeoForgeNetworkManager : NetworkManager {

    const val PROTOCOL_VERSION = "1.0.0"
    private var id = 0
    var registrar: PayloadRegistrar? = null

    override fun registerClientBound() {
        CobblemonNetwork.registerClientBound()
    }

    override fun registerServerBound() {
        CobblemonNetwork.registerServerBound()
    }

    @Suppress("INACCESSIBLE_TYPE")
    override fun <T : NetworkPacket<T>> createClientBound(
        identifier: Identifier,
        kClass: KClass<T>,
        encoder: (T, PacketByteBuf) -> Unit,
        decoder: (PacketByteBuf) -> T,
        handler: ClientNetworkPacketHandler<T>
    ) {
        val codec = object : PacketCodec<PacketByteBuf, T> {
            override fun decode(buf: PacketByteBuf): T {
                return decoder.invoke(buf)
            }

            override fun encode(buf: PacketByteBuf, value: T) {
                encoder.invoke(value, buf)
            }

        }
        val nfHandler = object : IPayloadHandler<T> {
            override fun handle(arg: T, iPayloadContext: IPayloadContext) {
                handler.handleOnNettyThread(arg)
            }

        }
        registrar?.playToClient(
            CustomPayload.id(identifier.path),
            codec,
            nfHandler
        )
    }

    @Suppress("INACCESSIBLE_TYPE")
    override fun <T : NetworkPacket<T>> createServerBound(
        identifier: Identifier,
        kClass: KClass<T>,
        encoder: (T, PacketByteBuf) -> Unit,
        decoder: (PacketByteBuf) -> T,
        handler: ServerNetworkPacketHandler<T>
    ) {
        val codec = object : PacketCodec<PacketByteBuf, T> {
            override fun decode(buf: PacketByteBuf): T {
                return decoder.invoke(buf)
            }

            override fun encode(buf: PacketByteBuf, value: T) {
                encoder.invoke(value, buf)
            }

        }
        val nfHandler = object : IPayloadHandler<T> {
            override fun handle(arg: T, iPayloadContext: IPayloadContext) {
                val player = iPayloadContext.player() as ServerPlayerEntity
                handler.handle(arg, player.server, player)
            }

        }
    }

    override fun sendPacketToPlayer(player: ServerPlayerEntity, packet: NetworkPacket<*>) {
        player.networkHandler.send(packet)
    }

    override fun sendPacketToServer(packet: NetworkPacket<*>) {
        MinecraftClient.getInstance().networkHandler?.send(packet)
    }

    override fun <T : NetworkPacket<*>> asVanillaClientBound(packet: T): Packet<ClientPlayPacketListener> {
        Cobblemon.LOGGER.error("THIS NEEDS TO BE FIXED!!!!!!!!")
        return DifficultyS2CPacket(Difficulty.NORMAL, false)
    }
}