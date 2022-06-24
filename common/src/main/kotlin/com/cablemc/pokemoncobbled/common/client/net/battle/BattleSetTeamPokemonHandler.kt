package com.cablemc.pokemoncobbled.common.client.net.battle

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.net.PacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.battle.BattleSetTeamPokemonPacket
import net.minecraft.client.MinecraftClient

object BattleSetTeamPokemonHandler : PacketHandler<BattleSetTeamPokemonPacket> {
    override fun invoke(packet: BattleSetTeamPokemonPacket, ctx: CobbledNetwork.NetworkContext) {
        MinecraftClient.getInstance().execute {
            PokemonCobbledClient.battle!!.side1.actors
                .find { it.uuid == MinecraftClient.getInstance().player?.uuid }
                ?.pokemon = packet.team
        }
    }
}