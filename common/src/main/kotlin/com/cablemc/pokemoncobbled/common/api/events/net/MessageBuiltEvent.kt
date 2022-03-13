package com.cablemc.pokemoncobbled.common.api.events.net

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket

/**
 * Fired when a new packet is being initialized and a handler should be applied. Internal use only!
 *
 * @author Hiroku
 * @since November 27th, 2021
 */
class MessageBuiltEvent<T : NetworkPacket>(
    val clazz: Class<T>,
    val isToServer: Boolean,
    val messageBuilder: CobbledNetwork.PreparedMessage<T>
)