package com.cablemc.pokemoncobbled.common.client.net.settings

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.settings.ServerSettingsPacket

object ServerSettingsPacketHandler : ClientPacketHandler<ServerSettingsPacket> {

    override fun invokeOnClient(packet: ServerSettingsPacket, ctx: CobbledNetwork.NetworkContext) {
        PokemonCobbled.config.apply {
            preventCompletePartyDeposit = packet.preventCompletePartyDeposit
        }
    }

}