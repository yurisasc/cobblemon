package com.cablemc.pokemoncobbled.common.client.net.battle

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.net.PacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.battle.BattleMakeChoicePacket

object BattleMakeChoiceHandler : PacketHandler<BattleMakeChoicePacket> {
    override fun invoke(packet: BattleMakeChoicePacket, ctx: CobbledNetwork.NetworkContext) {
        val battle = PokemonCobbledClient.battle ?: return
        PokemonCobbledClient.battleOverlay.passedSeconds = 0F
        battle.mustChoose = true
    }
}