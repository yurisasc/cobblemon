/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokedex

import com.cobblemon.mod.common.api.events.battles.BattleStartedPostEvent
import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent
import com.cobblemon.mod.common.api.events.pokemon.TradeCompletedEvent
import com.cobblemon.mod.common.api.events.pokemon.evolution.EvolutionCompleteEvent
import com.cobblemon.mod.common.api.pokedex.trackeddata.EventTriggerType
import com.cobblemon.mod.common.api.pokedex.trackeddata.GlobalTrackedData
import com.cobblemon.mod.common.api.storage.player.InstancedPlayerData
import com.cobblemon.mod.common.api.storage.player.client.ClientInstancedPlayerData
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor
import com.cobblemon.mod.common.battles.actor.PokemonBattleActor
import com.cobblemon.mod.common.config.pokedex.PokedexConfig
import com.cobblemon.mod.common.pokemon.FormData
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.getPlayer
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
        val speciesEntry = getSpeciesEntry(speciesId)
        speciesEntry.pokemonCaught(event)
    }

    fun pokemonEvolved(event: EvolutionCompleteEvent) {
        var haveUpdated = false
        if (!gennedFactories.contains(EventTriggerType.EVOLVE)) {
            genFactories(EventTriggerType.EVOLVE)
        }
        globalTrackedData.forEach {
            val changed = it.onEvolve(event)
            if (changed) {
                haveUpdated = true
            }
        }
        val speciesId = event.pokemon.species.resourceIdentifier
        val speciesEntry = getSpeciesEntry(speciesId)
        speciesEntry.pokemonEvolved(event)
    }

    fun pokemonTraded(event: TradeCompletedEvent) {
        var haveUpdated = false
        if (!gennedFactories.contains(EventTriggerType.TRADE)) {
            genFactories(EventTriggerType.TRADE)
        }
        globalTrackedData.forEach {
            val changed = it.onTrade(event)
            if (changed) {
                haveUpdated = true
            }
        }
        val recievedPokemon = if (event.tradeParticipant1.uuid == uuid) event.tradeParticipant2Pokemon else event.tradeParticipant1Pokemon
        val speciesEntry = getSpeciesEntry(recievedPokemon.species.resourceIdentifier)
        speciesEntry.pokemonTraded(event, uuid)
    }

    fun battleStart(event: BattleStartedPostEvent) {
        var haveUpdated = false
        if (!gennedFactories.contains(EventTriggerType.BATTLE_START)) {
            genFactories(EventTriggerType.BATTLE_START)
        }
        val player = uuid.getPlayer() ?: return
        globalTrackedData.forEach {
            val changed = it.onBattleStart(event)
            if (changed) {
                haveUpdated = true
            }
        }
        event.battle.actors.forEach {
            if (!it.isForPlayer(player)) {
                if (it is PokemonBattleActor) {
                    val pokemon = it.pokemon.originalPokemon
                    val speciesId = pokemon.species.resourceIdentifier
                    val formStr = pokemon.form.formOnlyShowdownId()
                    onPokemonSeen(speciesId, formStr)
                    val speciesEntry = getSpeciesEntry(speciesId)
                    speciesEntry.pokemonSeen(speciesId, formStr)
                }
                //Ideally we would not trigger the seen stuff on unseen pokemon but I don't think we currently have a way to listen to
                //Sendout events in battle (though I have not looked that hard!)
                else if (it is PlayerBattleActor) {
                    it.pokemonList.forEach { mon ->
                        val pokemon = mon.originalPokemon
                        val speciesId = pokemon.species.resourceIdentifier
                        val formStr = pokemon.form.formOnlyShowdownId()
                        onPokemonSeen(speciesId, formStr)
                        val speciesEntry = getSpeciesEntry(speciesId)
                        speciesEntry.pokemonSeen(speciesId, formStr)
                    }

                }
            }
        }

    }

    //This can be triggered by multiple things, like a pokemon switching in inside of a player battle
    fun onPokemonSeen(speciesId: Identifier, formStr: String) {

    }

    //Whenever a particular type of event is called we want to add all the tracked data that might rely on the event
    //Also doubles to update player's dex in case of config update (adds prev untracked data)
    private fun genFactories(type: EventTriggerType) {
        PokedexConfig.global.forEach {
            if (it.triggerEvents.contains(type)) {
                val anyMatch = globalTrackedData.any { curAdded ->
                    it == curAdded
                }
                if (!anyMatch) {
                    globalTrackedData.add(it.clone())
                }
            }
        }
        gennedFactories.add(type)
    }

    private fun getSpeciesEntry(speciesId: Identifier): SpeciesPokedexEntry {
        if (!speciesEntries.containsKey(speciesId)) {
            val newSpeciesEntry = SpeciesPokedexEntry()
            speciesEntries[speciesId] = newSpeciesEntry
        }
        return speciesEntries[speciesId]!!
    }

    override fun toClientData(): ClientInstancedPlayerData {
        return ClientPokedex(speciesEntries, globalTrackedData)
    }

    companion object {
        fun formToFormString(form: FormData, shiny: Boolean): String = if (shiny) form.name + "_shiny" else form.name
    }
}