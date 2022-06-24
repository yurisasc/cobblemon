package com.cablemc.pokemoncobbled.common.client.net.battle

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.battle.animations.MoveTileOffscreenAnimation
import com.cablemc.pokemoncobbled.common.net.PacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.battle.BattleFaintPacket

object BattleFaintHandler : PacketHandler<BattleFaintPacket> {
    override fun invoke(packet: BattleFaintPacket, ctx: CobbledNetwork.NetworkContext) {
        PokemonCobbledClient.battle?.getPokemonFromPNX(packet.pnx)?.second?.animations?.add(MoveTileOffscreenAnimation())
    }
}