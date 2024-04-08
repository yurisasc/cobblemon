/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.starter

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.advancement.CobblemonCriteria
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.starter.StarterChosenEvent
import com.cobblemon.mod.common.api.starter.RenderableStarterCategory
import com.cobblemon.mod.common.api.starter.StarterCategory
import com.cobblemon.mod.common.api.starter.StarterHandler
import com.cobblemon.mod.common.api.starter.StarterRegistry
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.net.messages.client.starter.OpenStarterUIPacket
import com.cobblemon.mod.common.util.lang
import com.cobblemon.mod.common.world.gamerules.CobblemonGameRules
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

open class CobblemonStarterHandler : StarterHandler {

    override fun getStarters(player: ServerPlayerEntity): Map<Identifier, StarterCategory> = StarterRegistry.all()

    // No OP the starters are sent out when the open UI packet is sent out instead
    override fun handleJoin(player: ServerPlayerEntity) {}

    override fun requestStarterChoice(player: ServerPlayerEntity) {
        if (!this.isAbleToSelect(player)) {
            return
        }
        val playerData = Cobblemon.playerData.get(player)
        val renderable = this.getStarters(player).entries.associate { (id, category) ->
            val renderable = category.pokemon.map {
                // Let the actual Pokémon be created and apply the shiny accordingly
                // This won't be accurate with random but I mean what else do you want me to do
                val pokemon = it.create()
                // While they can still be shiny a random shiny from the #create doesn't mean the actual starter will be shiny
                // This tries to be realistic
                pokemon.shiny = player.world.gameRules.getBoolean(CobblemonGameRules.SHINY_STARTERS)
                pokemon.asRenderablePokemon()
            }
            id to RenderableStarterCategory(category.displayName, renderable)
        }
        OpenStarterUIPacket(renderable).sendToPlayer(player)
        playerData.starterPrompted = true
        Cobblemon.playerData.saveSingle(playerData)
    }

    override fun chooseStarter(player: ServerPlayerEntity, categoryId: Identifier, index: Int) {
        if (!this.isAbleToSelect(player)) {
            return
        }
        val category = this.getStarters(player)[categoryId] ?: return
        val properties = category.pokemon.getOrNull(index) ?: return
        val pokemon = properties.create()
        val playerData = Cobblemon.playerData.get(player)
        CobblemonEvents.STARTER_CHOSEN.postThen(StarterChosenEvent(player, properties, pokemon)) { event ->
            Cobblemon.storage.getParty(player).add(
                event.pokemon.also {
                    playerData.starterSelected = true
                    playerData.starterUUID = it.uuid
                    if (player.world.gameRules.getBoolean(CobblemonGameRules.SHINY_STARTERS)) { pokemon.shiny = true }
                }
            )
            CobblemonCriteria.PICK_STARTER.trigger(player, pokemon)
            Cobblemon.playerData.saveSingle(playerData)
            playerData.sendToPlayer(player)
        }
    }

    /**
     * Checks if the [player] can select a starter.
     * If not gives them an appropriate feedback message.
     *
     * @param player The [ServerPlayerEntity] being tested.
     * @return If the [player] can pick a starter.
     */
    private fun isAbleToSelect(player: ServerPlayerEntity): Boolean {
        val playerData = Cobblemon.playerData.get(player)
        if (playerData.starterSelected) {
            player.sendMessage(lang("ui.starter.alreadyselected").red(), true)
            return false
        } else if (playerData.starterLocked) {
            player.sendMessage(lang("ui.starter.cannotchoose").red(), true)
            return false
        }
        return true
    }

}