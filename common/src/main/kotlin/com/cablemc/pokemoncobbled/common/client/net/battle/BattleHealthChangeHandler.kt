package com.cablemc.pokemoncobbled.common.client.net.battle

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.battle.animations.HealthChangeAnimation
import com.cablemc.pokemoncobbled.common.net.PacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.battle.BattleHealthChangePacket
import net.minecraft.client.MinecraftClient

object BattleHealthChangeHandler : PacketHandler<BattleHealthChangePacket> {
    override fun invoke(packet: BattleHealthChangePacket, ctx: CobbledNetwork.NetworkContext) {
        MinecraftClient.getInstance().execute {
            val battle = PokemonCobbledClient.battle ?: return@execute
            val (actor, activePokemon) = battle.getPokemonFromPNX(packet.pnx)
            activePokemon.animations.add(
                HealthChangeAnimation(newHealthRatio = packet.newHealthRatio)
            )
        }
    }
}