package com.cablemc.pokemoncobbled.common.net

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket

/**
 * A simple packet handler as an SAM interface. If this packet handler is invoked, it
 *
 * @author Hiroku
 * @since November 27th, 2021
 */
interface PacketHandler<T: NetworkPacket> {
    operator fun invoke(packet: T, ctx: CobbledNetwork.NetworkContext)
}

