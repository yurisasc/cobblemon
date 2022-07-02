package com.cablemc.pokemoncobbled.common.net.serverhandling.storage.party

import com.cablemc.pokemoncobbled.common.CobbledNetwork.NetworkContext
import com.cablemc.pokemoncobbled.common.net.messages.server.storage.party.SwapPartyPokemonPacket
import com.cablemc.pokemoncobbled.common.net.serverhandling.ServerPacketHandler
import com.cablemc.pokemoncobbled.common.util.party
import net.minecraft.server.network.ServerPlayerEntity

object SwapPartyPokemonHandler : ServerPacketHandler<SwapPartyPokemonPacket> {
    override fun invokeOnServer(packet: SwapPartyPokemonPacket, ctx: NetworkContext, player: ServerPlayerEntity) {
        val party = player.party()

        val pokemon1 = party[packet.position1] ?: return
        val pokemon2 = party[packet.position2] ?: return

        if (pokemon1.uuid != packet.pokemon1ID || pokemon2.uuid != packet.pokemon2ID || packet.position1 == packet.position2) {
            return
        }

        party.swap(packet.position1, packet.position2)
    }
}