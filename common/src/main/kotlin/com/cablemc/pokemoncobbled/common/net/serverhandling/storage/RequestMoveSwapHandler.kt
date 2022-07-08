package com.cablemc.pokemoncobbled.common.net.serverhandling.storage

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.net.messages.server.RequestMoveSwapPacket
import com.cablemc.pokemoncobbled.common.net.serverhandling.ServerPacketHandler
import net.minecraft.server.network.ServerPlayerEntity

object RequestMoveSwapHandler: ServerPacketHandler<RequestMoveSwapPacket> {
    override fun invokeOnServer(packet: RequestMoveSwapPacket, ctx: CobbledNetwork.NetworkContext, player: ServerPlayerEntity) {
        val pokemon = PokemonCobbled.storage.getParty(player).get(packet.slot) ?: return
        val move1 = pokemon.moveSet[packet.move1] ?: return
        val move2 = pokemon.moveSet[packet.move2] ?: return
        if (move1 == move2) {
            return
        }
        pokemon.moveSet.swapMove(packet.move1, packet.move2)
    }
}