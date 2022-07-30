package com.cablemc.pokemoncobbled.common.client.net.battle

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.battle.ClientBattlePokemon
import com.cablemc.pokemoncobbled.common.client.battle.animations.MoveTileOffscreenAnimation
import com.cablemc.pokemoncobbled.common.client.battle.animations.SwapAndMoveTileOnscreenAnimation
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.PokemonFloatingState
import com.cablemc.pokemoncobbled.common.net.messages.client.battle.BattleSwitchPokemonPacket

object BattleSwitchPokemonHandler : ClientPacketHandler<BattleSwitchPokemonPacket> {
    override fun invokeOnClient(packet: BattleSwitchPokemonPacket, ctx: CobbledNetwork.NetworkContext) {
        val battle = PokemonCobbledClient.battle ?: return
        val (actor, activeBattlePokemon) = battle.getPokemonFromPNX(packet.pnx)

        val lastAnimation = activeBattlePokemon.animations.lastOrNull()
        if (lastAnimation !is MoveTileOffscreenAnimation) {
            activeBattlePokemon.animations.add(MoveTileOffscreenAnimation())
        }

        activeBattlePokemon.animations.add(
            SwapAndMoveTileOnscreenAnimation(
                with(packet.newPokemon) {
                    ClientBattlePokemon(
                        uuid = uuid,
                        displayName = displayName,
                        properties = properties,
                        hpRatio = hpRatio,
                        status = status,
                        statChanges = statChanges
                    ).also {
                        it.actor = actor
                        it.state = PokemonFloatingState()
                    }
                }
            )
        )
    }
}