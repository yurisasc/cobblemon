package com.cablemc.pokemoncobbled.forge.mod.net

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.NetworkDelegate
import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.net.PacketHandler
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.server.level.ServerPlayerEntity
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.network.NetworkEvent
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.PacketDistributor
import net.minecraftforge.network.simple.SimpleChannel
import java.util.function.Supplier

class PreparedForgeMessage<T : NetworkPacket>(private val messageBuilder: SimpleChannel.MessageBuilder<T>) : CobbledNetwork.PreparedMessage<T> {
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

class ForgeNetworkContext(val ctx: NetworkEvent.Context) : CobbledNetwork.NetworkContext {
    override val player = ctx.sender
}

object CobbledForgeNetworkDelegate : NetworkDelegate {
    var discriminator = 0

    val channel = NetworkRegistry.newSimpleChannel(
        cobbledResource("main"),
        { CobbledNetwork.PROTOCOL_VERSION },
        CobbledNetwork.PROTOCOL_VERSION::equals,
        CobbledNetwork.PROTOCOL_VERSION::equals
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
    ): CobbledNetwork.PreparedMessage<T> {
        return PreparedForgeMessage(
            channel.messageBuilder(
                packetClass,
                discriminator++,
                if (toServer) NetworkDirection.PLAY_TO_SERVER else NetworkDirection.PLAY_TO_CLIENT
            )
                .encoder { packet, buffer -> packet.encode(buffer) }
                .decoder { buffer -> packetClass.getDeclaredConstructor().newInstance().also { it.decode(buffer) } }
        )
    }
}