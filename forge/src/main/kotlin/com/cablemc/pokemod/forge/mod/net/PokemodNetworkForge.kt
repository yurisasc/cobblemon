/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.forge.mod.net

import com.cablemc.pokemod.common.NetworkDelegate
import com.cablemc.pokemod.common.PokemodNetwork
import com.cablemc.pokemod.common.api.net.NetworkPacket
import com.cablemc.pokemod.common.net.PacketHandler
import com.cablemc.pokemod.common.util.pokemodResource
import java.util.function.Supplier
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.network.NetworkEvent
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.PacketDistributor
import net.minecraftforge.network.simple.SimpleChannel
class PreparedForgeMessage<T : NetworkPacket>(private val messageBuilder: SimpleChannel.MessageBuilder<T>) : PokemodNetwork.PreparedMessage<T> {
    override fun registerMessage() {
        messageBuilder.add()
    }

    override fun registerHandler(handler: PacketHandler<T>) {
        messageBuilder.consumer(SimpleChannel.MessageBuilder.ToBooleanBiFunction<T, Supplier<NetworkEvent.Context>> { packet, ctx ->
            handler(packet, ForgeNetworkContext(ctx.get()))
            return@ToBooleanBiFunction true
        })
    }
}
class ForgeNetworkContext(val ctx: NetworkEvent.Context) : PokemodNetwork.NetworkContext {
    override val player = ctx.sender
}

object PokemodForgeNetworkDelegate : NetworkDelegate {
    var discriminator = 0

    val channel = NetworkRegistry.newSimpleChannel(
        pokemodResource("main"),
        { PokemodNetwork.PROTOCOL_VERSION },
        PokemodNetwork.PROTOCOL_VERSION::equals,
        PokemodNetwork.PROTOCOL_VERSION::equals
    )

    override fun sendPacketToPlayer(player: ServerPlayerEntity, packet: NetworkPacket) {
        channel.send(PacketDistributor.PLAYER.with { player }, packet)
    }

    override fun sendPacketToServer(packet: NetworkPacket) {
        channel.sendToServer(packet)
    }

    override fun <T : NetworkPacket> buildMessage(
        packetClass: Class<T>,
        toServer: Boolean
    ): PokemodNetwork.PreparedMessage<T> {
        return PreparedForgeMessage(
            channel.messageBuilder(
                packetClass,
                discriminator++,
                if (toServer) NetworkDirection.PLAY_TO_SERVER else NetworkDirection.PLAY_TO_CLIENT
            )
                .encoder { packet, buffer -> packet.encode(buffer) }
                .decoder { buffer ->
                    println("We're unpacking a message: ${packetClass.simpleName} and it is toServer = $toServer")
                    packetClass.getDeclaredConstructor().newInstance().also { it.decode(buffer) } .also { println("Succeeded") }
                }
        )
    }
}