package com.cablemc.pokemoncobbled.common.client.net.battle

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.battle.BattleEndPacket

object BattleEndHandler : ClientPacketHandler<BattleEndPacket> {
    override fun invokeOnClient(packet: BattleEndPacket, ctx: CobbledNetwork.NetworkContext) = PokemonCobbledClient.endBattle()
}