/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.starter

import com.cablemc.pokemod.common.Pokemod
import com.cablemc.pokemod.common.advancement.PokemodCriteria
import com.cablemc.pokemod.common.api.events.PokemodEvents
import com.cablemc.pokemod.common.api.events.starter.StarterChosenEvent
import com.cablemc.pokemod.common.api.starter.StarterHandler
import com.cablemc.pokemod.common.api.text.red
import com.cablemc.pokemod.common.net.messages.client.starter.OpenStarterUIPacket
import com.cablemc.pokemod.common.util.lang
import net.minecraft.server.network.ServerPlayerEntity

open class CobbledStarterHandler : StarterHandler {

    override fun getStarterList(player: ServerPlayerEntity) = Pokemod.starterConfig.starters

    override fun handleJoin(player: ServerPlayerEntity) {}

    override fun requestStarterChoice(player: ServerPlayerEntity) {
        val playerData = Pokemod.playerData.get(player)
        if (playerData.starterSelected) {
            playerData.sendToPlayer(player)
            player.sendMessage(lang("ui.starter.alreadyselected").red())
        } else if (playerData.starterLocked) {
            player.sendMessage(lang("ui.starter.cannotchoose").red())
        } else {
            OpenStarterUIPacket(getStarterList(player)).sendToPlayer(player)
            playerData.starterPrompted = true
            Pokemod.playerData.saveSingle(playerData)
        }
    }

    override fun chooseStarter(player: ServerPlayerEntity, categoryName: String, index: Int) {
        val playerData = Pokemod.playerData.get(player)
        if (playerData.starterSelected) {
            return player.sendMessage(lang("ui.starter.alreadyselected").red())
        } else if (playerData.starterLocked) {
            return player.sendMessage(lang("ui.starter.cannotchoose").red())
        }

        val category = getStarterList(player).find { it.name == categoryName } ?: return

        if (index > category.pokemon.size) {
            return
        }

        val properties = category.pokemon[index]
        val pokemon = properties.create()

        PokemodEvents.STARTER_CHOSEN.postThen(StarterChosenEvent(player, properties, pokemon)) {
            Pokemod.storage.getParty(player).add(
                it.pokemon.also {
                    playerData.starterSelected = true
                    playerData.starterUUID = it.uuid
                }
            )
            PokemodCriteria.PICK_STARTER.trigger(player, pokemon)
            Pokemod.playerData.saveSingle(playerData)
            playerData.sendToPlayer(player)
        }
    }

}