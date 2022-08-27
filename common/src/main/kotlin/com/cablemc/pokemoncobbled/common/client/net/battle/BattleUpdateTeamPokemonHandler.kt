package com.cablemc.pokemoncobbled.common.client.net.battle

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.battle.BattleUpdateTeamPokemonPacket
import net.minecraft.client.MinecraftClient

object BattleUpdateTeamPokemonHandler : ClientPacketHandler<BattleUpdateTeamPokemonPacket> {
    override fun invokeOnClient(packet: BattleUpdateTeamPokemonPacket, ctx: CobbledNetwork.NetworkContext) {
        val battle = PokemonCobbledClient.battle ?: return
        val actor = battle.side1.actors.find { it.uuid == MinecraftClient.getInstance().player?.uuid }
        if (actor != null) {
            val previous = actor.pokemon.find { it.uuid == packet.pokemon.uuid }
            if (previous !=  null) {
                actor.pokemon.add(actor.pokemon.indexOf(previous), packet.pokemon)
                actor.pokemon.remove(previous)
            }
        }
    }
}