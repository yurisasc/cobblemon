package com.cablemc.pokemoncobbled.common.net

import com.cablemc.pokemoncobbled.common.api.events.CobbledEvents
import com.cablemc.pokemoncobbled.common.api.events.net.MessageBuiltEvent
import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.api.reactive.Observable.Companion.takeFirst

/**
 * Registers packet handlers for a particular side. It's a bit hellish because of a desire for generic type conformity
 * and some inanity in the way Forge built this rubbish.
 *
 * @author Hiroku
 * @since November 27th, 2021
 */
abstract class SidedPacketRegistrar {
    abstract fun registerHandlers()

    init {
        CobbledEvents.MESSAGE_BUILT.pipe(takeFirst()).subscribe { on(it) }
    }

    fun on(event: MessageBuiltEvent<*>) {
        handleEvent(event)
    }

    protected val packetHandlerRegistrations = mutableMapOf<Class<*>, (MessageBuiltEvent<*>) -> Unit>()

    fun handleEvent(event: MessageBuiltEvent<*>) {
        packetHandlerRegistrations[event.clazz ]?.invoke(event)
    }

    inline fun <reified T : NetworkPacket> register(event: MessageBuiltEvent<T>, handler: PacketHandler<T>) {
        event.messageBuilder.registerHandler(handler)
    }

    protected inline fun <reified T : NetworkPacket> registerHandler(handler: PacketHandler<T>) {
        onRegistering<T> { register(it as MessageBuiltEvent<T>, handler) }
    }

    protected inline fun <reified P: NetworkPacket> onRegistering(noinline handler: (MessageBuiltEvent<*>) -> Unit) {
        packetHandlerRegistrations[P::class.java] = handler
    }
}