package com.cablemc.pokemoncobbled.common.net.serverhandling.storage

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.net.PacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.server.RequestMoveSwapPacket

object RequestMoveSwapHandler: PacketHandler<RequestMoveSwapPacket> {
    override fun invoke(packet: RequestMoveSwapPacket, ctx: CobbledNetwork.NetworkContext) {
        val player = ctx.player ?: return
        val pokemon = PokemonCobbled.storage.getParty(player).get(packet.slot) ?: return
        // TODO validate the move slots are legit and that both slots have a move in it
        pokemon.moveSet.swapMove(packet.move1, packet.move2)
    }
}