package com.cablemc.pokemoncobbled.common.client.net.battle

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.net.PacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.battle.BattleEndPacket

object BattleEndPacketHandler : PacketHandler<BattleEndPacket> {
    override fun invoke(packet: BattleEndPacket, ctx: CobbledNetwork.NetworkContext) = PokemonCobbledClient.endBattle()
}