package com.cablemc.pokemoncobbled.common.api.net

import com.cablemc.pokemoncobbled.common.api.event.net.MessageBuiltEvent
import com.cablemc.pokemoncobbled.common.net.PacketHandler
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fmllegacy.network.NetworkEvent
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel
import java.util.function.Supplier

/**
 * Registers packet handlers for a particular side. It's a bit hellish because of a desire for generic type conformity
 * and some inanity in the way Forge built this rubbish.
 *
 * @author Hiroku
 * @since November 27th, 2021
 */
abstract class SidedPacketRegistrar {
    abstract fun registerHandlers()

    @SubscribeEvent
    fun on(event: MessageBuiltEvent<*>) {
        handleEvent(event)
    }

    protected val packetHandlerRegistrations = mutableMapOf<Class<*>, (MessageBuiltEvent<*>) -> Unit>()

    fun handleEvent(event: MessageBuiltEvent<*>) {
        packetHandlerRegistrations[event.clazz ]?.invoke(event)
    }

    inline fun <reified T : NetworkPacket> register(event: MessageBuiltEvent<T>, handler: PacketHandler<T>) {
        event.messageBuilder.consumer(SimpleChannel.MessageBuilder.ToBooleanBiFunction<T, Supplier<NetworkEvent.Context>> { packet, ctx ->
            handler(packet, ctx.get())
            return@ToBooleanBiFunction true
        })
    }

    protected inline fun <reified T : NetworkPacket> registerHandler(handler: PacketHandler<T>) {
        onRegistering<T> { register(it as MessageBuiltEvent<T>, handler) }
    }

    protected inline fun <reified P: NetworkPacket> onRegistering(noinline handler: (MessageBuiltEvent<*>) -> Unit) {
        packetHandlerRegistrations[P::class.java] = handler
    }
}