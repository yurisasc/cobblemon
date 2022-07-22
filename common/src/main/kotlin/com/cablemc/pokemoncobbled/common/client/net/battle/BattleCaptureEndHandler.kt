package com.cablemc.pokemoncobbled.common.client.net.battle

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.api.scheduling.after
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.battle.animations.MoveTileOffscreenAnimation
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.battle.BattleCaptureEndPacket

object BattleCaptureEndHandler : ClientPacketHandler<BattleCaptureEndPacket> {
    override fun invokeOnClient(packet: BattleCaptureEndPacket, ctx: CobbledNetwork.NetworkContext) {
        val battle = PokemonCobbledClient.battle ?: return
        val (_, activeBattlePokemon) = battle.getPokemonFromPNX(packet.targetPNX)
        if (packet.succeeded) {
            activeBattlePokemon.animations.add(MoveTileOffscreenAnimation().also { after(seconds = it.duration) { activeBattlePokemon.ballCapturing = null } })
        } else {
            activeBattlePokemon.ballCapturing?.finish()
        }
    }
}