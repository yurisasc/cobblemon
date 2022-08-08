package com.cablemc.pokemoncobbled.common.starter

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.events.CobbledEvents
import com.cablemc.pokemoncobbled.common.api.events.starter.StarterChosenEvent
import com.cablemc.pokemoncobbled.common.api.starter.StarterHandler
import com.cablemc.pokemoncobbled.common.api.text.red
import com.cablemc.pokemoncobbled.common.net.messages.client.starter.OpenStarterUIPacket
import com.cablemc.pokemoncobbled.common.util.lang
import com.cablemc.pokemoncobbled.common.util.sendServerMessage
import net.minecraft.server.network.ServerPlayerEntity

open class CobbledStarterHandler : StarterHandler {

    override fun getStarterList(player: ServerPlayerEntity) = PokemonCobbled.starterConfig.starters

    override fun handleJoin(player: ServerPlayerEntity) {}

    override fun requestStarterChoice(player: ServerPlayerEntity) {
        val playerData = PokemonCobbled.playerData.get(player)
        if (playerData.starterSelected) {
            playerData.sendToPlayer(player)
            player.sendServerMessage(lang("ui.starter.alreadyselected").red())
        } else if (playerData.starterLocked) {
            player.sendServerMessage(lang("ui.starter.cannotchoose").red())
        } else {
            OpenStarterUIPacket(getStarterList(player)).sendToPlayer(player)
            playerData.starterPrompted = true
            PokemonCobbled.playerData.saveSingle(playerData)
        }
    }

    override fun chooseStarter(player: ServerPlayerEntity, categoryName: String, index: Int) {
        val playerData = PokemonCobbled.playerData.get(player)
        if (playerData.starterSelected) {
            return player.sendServerMessage(lang("ui.starter.alreadyselected").red())
        } else if (playerData.starterLocked) {
            return player.sendServerMessage(lang("ui.starter.cannotchoose").red())
        }

        val category = getStarterList(player).find { it.name == categoryName } ?: return

        if (index > category.pokemon.size) {
            return
        }

        val properties = category.pokemon[index]
        val pokemon = properties.create()

        CobbledEvents.STARTER_CHOSEN.postThen(StarterChosenEvent(player, properties, pokemon)) {
            PokemonCobbled.storage.getParty(player).add(
                it.pokemon.also {
                    playerData.starterSelected = true
                    playerData.starterUUID = it.uuid
                }
            )

            PokemonCobbled.playerData.saveSingle(playerData)
            playerData.sendToPlayer(player)
        }
    }

}