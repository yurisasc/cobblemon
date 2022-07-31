package com.cablemc.pokemoncobbled.common.net.serverhandling.storage

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.net.messages.server.SelectStarterPacket
import com.cablemc.pokemoncobbled.common.net.serverhandling.ServerPacketHandler
import com.cablemc.pokemoncobbled.common.util.asTranslated
import com.cablemc.pokemoncobbled.common.util.sendServerMessage
import net.minecraft.server.network.ServerPlayerEntity

object SelectStarterPacketHandler : ServerPacketHandler<SelectStarterPacket> {
    override fun invokeOnServer(
        packet: SelectStarterPacket,
        ctx: CobbledNetwork.NetworkContext,
        player: ServerPlayerEntity
    ) {
        val category = PokemonCobbled.config.starters.find { it.name == packet.categoryName }
            ?: return

        val selection = packet.selected

        if (selection > category.pokemon.size)
            return

        val playerData = PokemonCobbled.playerData.get(player)

        if (playerData.starterSelected) {
            player.sendServerMessage("pokemoncobbled.ui.starter.alreadyselected".asTranslated())
            return
        }

        PokemonCobbled.storage.getParty(player).add(
            category.pokemon[selection].create().also {
                playerData.starterSelected = true
                playerData.starterUUID = it.uuid
            }
        )

        PokemonCobbled.playerData.saveSingle(playerData)
    }
}