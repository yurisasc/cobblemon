package com.cablemc.pokemoncobbled.client.net

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.net.PacketHandler
import com.cablemc.pokemoncobbled.common.util.ifClient
import net.minecraftforge.network.NetworkEvent

/*
 * A packet handler which will queue and safely execute the invocation on the physical client thread.
 *
 * @author Hiroku
 * @since November 28th, 2021
 */
interface ClientPacketHandler<T : NetworkPacket> : PacketHandler<T> {
    override fun invoke(packet: T, ctx: NetworkEvent.Context) {
        ctx.enqueueWork { ifClient { invokeOnClient(packet, ctx) } }
    }

    fun invokeOnClient(packet: T, ctx: NetworkEvent.Context)
}