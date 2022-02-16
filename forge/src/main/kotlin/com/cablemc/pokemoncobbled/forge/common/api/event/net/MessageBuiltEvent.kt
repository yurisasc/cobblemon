package com.cablemc.pokemoncobbled.forge.common.api.event.net

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import net.minecraftforge.eventbus.api.Event
import net.minecraftforge.network.simple.SimpleChannel

/**
 * Fired when a new packet is being initialized and a handler should be applied. Internal use only!
 *
 * @author Hiroku
 * @since November 27th, 2021
 */
class MessageBuiltEvent<T : NetworkPacket>(val clazz: Class<T>, val messageBuilder: SimpleChannel.MessageBuilder<T>) : Event()