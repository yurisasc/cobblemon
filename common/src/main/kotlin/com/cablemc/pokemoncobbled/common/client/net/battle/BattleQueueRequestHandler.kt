package com.cablemc.pokemoncobbled.common.client.net.battle

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.battle.SingleActionRequest
import com.cablemc.pokemoncobbled.common.net.PacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.battle.BattleQueueRequestPacket
import net.minecraft.client.MinecraftClient

object BattleQueueRequestHandler : PacketHandler<BattleQueueRequestPacket> {
    override fun invoke(packet: BattleQueueRequestPacket, ctx: CobbledNetwork.NetworkContext) {
        MinecraftClient.getInstance().execute {
            val battle = PokemonCobbledClient.battle ?: return@execute
            val actor = battle.side1.actors.find { it.uuid == MinecraftClient.getInstance().player?.uuid } ?: return@execute
            PokemonCobbledClient.battle?.pendingActionRequests?.addAll(SingleActionRequest.composeFrom(actor, packet.request))
        }
    }
}