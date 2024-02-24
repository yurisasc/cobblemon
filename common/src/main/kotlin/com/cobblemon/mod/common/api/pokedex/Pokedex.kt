/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokedex

import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent
import com.cobblemon.mod.common.api.pokedex.trackeddata.EventTriggerType
import com.cobblemon.mod.common.api.pokedex.trackeddata.GlobalTrackedData
import com.cobblemon.mod.common.api.storage.player.InstancedPlayerData
import com.cobblemon.mod.common.api.storage.player.client.ClientInstancedPlayerData
import com.cobblemon.mod.common.config.pokedex.PokedexConfig
import com.cobblemon.mod.common.pokemon.FormData
import net.minecraft.util.Identifier
import java.util.UUID

/**
 * Stores encounter statuses for each pokemon form, and additional configurable stats
 *
 * @since February 24, 2024
 * @author Apion
 */
class Pokedex(override val uuid: UUID) : InstancedPlayerData {
    val speciesEntries = mutableMapOf<Identifier, SpeciesPokedexEntry>()
    val globalTrackedData = mutableSetOf<GlobalTrackedData>()
    //GSON sets this field to null when deserializing even when marked transient sigh
    @Transient
    var gennedFactories = mutableSetOf<EventTriggerType>()
    fun pokemonCaught(event: PokemonCapturedEvent) {
        var haveUpdated = false
        if (!gennedFactories.contains(EventTriggerType.CAUGHT)) {
            genFactories(EventTriggerType.CAUGHT)
        }
        globalTrackedData.forEach {
            val changed = it.onCatch(event)
            if (changed) {
                haveUpdated = true
            }
        }
        val speciesId = event.pokemon.species.resourceIdentifier
        if (!speciesEntries.containsKey(speciesId)) {
            val newSpeciesEntry = SpeciesPokedexEntry()
            speciesEntries[speciesId] = newSpeciesEntry
        }
        val speciesEntry = speciesEntries[speciesId]!!
        speciesEntry.pokemonCaught(event)
    }
    //Whenever a particular type of event is called we want to add all the tracked data that might rely on the event
    //Also doubles to update player's dex in case of config update (adds prev untracked data)
    private fun genFactories(type: EventTriggerType) {
        PokedexConfig.global.forEach {
            if (it.triggerEvents.contains(type)) {
                val anyMatch = globalTrackedData.any { curAdded ->
                    curAdded::class.java == it::class.java
                }
                if (!anyMatch) {
                    globalTrackedData.add(it.clone())
                }
            }
        }
        gennedFactories.add(type)
    }

    override fun toClientData(): ClientInstancedPlayerData {
        return ClientPokedex(speciesEntries, globalTrackedData)
    }

    companion object {
        fun formToFormString(form: FormData, shiny: Boolean): String = if (shiny) form.name + "_shiny" else form.name
    }
}