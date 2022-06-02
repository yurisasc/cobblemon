package com.cablemc.pokemoncobbled.common.client.net.battle

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.net.PacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.battle.BattleQueueRequestPacket

object BattleQueueRequestHandler : PacketHandler<BattleQueueRequestPacket> {
    override fun invoke(packet: BattleQueueRequestPacket, ctx: CobbledNetwork.NetworkContext) {
        PokemonCobbledClient.battle?.actionRequest = packet.request
    }
}