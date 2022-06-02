package com.cablemc.pokemoncobbled.fabric.net

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.NetworkDelegate
import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.net.PacketHandler
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity

abstract class PreparedFabricMessage<T : NetworkPacket>(protected val registeredMessage: RegisteredMessage<T>) : CobbledNetwork.PreparedMessage<T> {
    override fun registerMessage() {}
}

class PreparedClientBoundFabricMessage<T : NetworkPacket>(registeredMessage: RegisteredMessage<T>) : PreparedFabricMessage<T>(registeredMessage) {
    override fun registerHandler(handler: PacketHandler<T>) {
        ClientPlayNetworking.registerGlobalReceiver(
            registeredMessage.identifier
        ) { _, _, buf, _ ->
            val packet = registeredMessage.packetClass.getDeclaredConstructor().newInstance().also { it.decode(buf) }
            val context = FabricClientNetworkContext()
            handler(packet, context)
        }
    }
}

class PreparedServerBoundFabricMessage<T : NetworkPacket>(registeredMessage: RegisteredMessage<T>) : PreparedFabricMessage<T>(registeredMessage) {
    override fun registerHandler(handler: PacketHandler<T>) {
        ServerPlayNetworking.registerGlobalReceiver(
            registeredMessage.identifier
        ) { _, player, _, buf, _ ->
            val packet = registeredMessage.packetClass.getDeclaredConstructor().newInstance().also { it.decode(buf) }
            val context = FabricServerNetworkContext(player)
            handler(packet, context)
        }
    }
}

class FabricServerNetworkContext(override val player: ServerPlayerEntity) : CobbledNetwork.NetworkContext
class FabricClientNetworkContext(override val player: ServerPlayerEntity? = null) : CobbledNetwork.NetworkContext

class RegisteredMessage<T : NetworkPacket>(
    val packetClass: Class<T>
) {
    val identifier = cobbledResource(packetClass.simpleName.lowercase())
    fun encode(packet: NetworkPacket): PacketByteBuf? {
        if (packetClass.isInstance(packet)) {
            return PacketByteBuf(Unpooled.buffer()).also { packet.encode(it) }
        }
        return null
    }
}

object CobbledFabricNetworkDelegate : NetworkDelegate {
    val registeredMessages = mutableListOf<RegisteredMessage<*>>()

    override fun sendPacketToPlayer(player: ServerPlayerEntity, packet: NetworkPacket) {
        val registeredMessage = registeredMessages.find { it.packetClass.isInstance(packet) }
            ?: throw IllegalStateException("There was no registered message for packet of type ${packet::class.simpleName}!")
        ServerPlayNetworking.send(player, registeredMessage.identifier, registeredMessage.encode(packet))
    }

    override fun sendPacketToServer(packet: NetworkPacket) {
        val registeredMessage = registeredMessages.find { it.packetClass.isInstance(packet) }
            ?: throw IllegalStateException("There was no registered message for packet of type ${packet::class.simpleName}!")
        ClientPlayNetworking.send(registeredMessage.identifier, registeredMessage.encode(packet))
    }

    override fun <T : NetworkPacket> buildMessage(
        packetClass: Class<T>,
        toServer: Boolean
    ): CobbledNetwork.PreparedMessage<T> {
        val registeredMessage = RegisteredMessage(packetClass)
        registeredMessages.add(registeredMessage)

        return if (toServer) {
            PreparedServerBoundFabricMessage(registeredMessage)
        } else {
            PreparedClientBoundFabricMessage(registeredMessage)
        }
    }
}