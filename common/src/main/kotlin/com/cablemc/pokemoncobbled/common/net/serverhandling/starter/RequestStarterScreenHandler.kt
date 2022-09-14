package com.cablemc.pokemoncobbled.common.net.serverhandling.starter

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.text.red
import com.cablemc.pokemoncobbled.common.net.messages.server.starter.RequestStarterScreenPacket
import com.cablemc.pokemoncobbled.common.net.serverhandling.ServerPacketHandler
import com.cablemc.pokemoncobbled.common.util.lang
import net.minecraft.server.network.ServerPlayerEntity

object RequestStarterScreenHandler : ServerPacketHandler<RequestStarterScreenPacket> {
    override fun invokeOnServer(packet: RequestStarterScreenPacket, ctx: CobbledNetwork.NetworkContext, player: ServerPlayerEntity) {
        val playerData = PokemonCobbled.playerData.get(player)

        if (playerData.starterSelected) {
            return player.sendMessage(lang("ui.starter.alreadyselected").red())
        } else if (playerData.starterLocked) {
            return player.sendMessage(lang("ui.starter.cannotchoose").red())
        } else {
            PokemonCobbled.starterHandler.requestStarterChoice(player)
        }
    }
}