package com.cablemc.pokemoncobbled.common.net.serverhandling.starter

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.net.messages.server.SelectStarterPacket
import com.cablemc.pokemoncobbled.common.net.serverhandling.ServerPacketHandler
import com.cablemc.pokemoncobbled.common.util.asTranslated
import com.cablemc.pokemoncobbled.common.util.lang
import com.cablemc.pokemoncobbled.common.util.sendServerMessage
import net.minecraft.server.network.ServerPlayerEntity

object SelectStarterPacketHandler : ServerPacketHandler<SelectStarterPacket> {
    override fun invokeOnServer(packet: SelectStarterPacket, ctx: CobbledNetwork.NetworkContext, player: ServerPlayerEntity) {
        PokemonCobbled.starterHandler.chooseStarter(player, packet.categoryName, packet.selected)
    }
}