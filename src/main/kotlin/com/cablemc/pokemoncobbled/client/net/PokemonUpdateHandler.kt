package com.cablemc.pokemoncobbled.client.net

import com.cablemc.pokemoncobbled.common.net.PacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.PokemonUpdatePacket
import net.minecraftforge.fmllegacy.network.NetworkEvent

object PokemonUpdateHandler : PacketHandler<PokemonUpdatePacket> {
    override fun invoke(packet: PokemonUpdatePacket, ctx: NetworkEvent.Context) {
        println("Hi")
        // TODO an actual update handler
    }
}