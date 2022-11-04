/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.events

import com.cablemc.pokemod.common.Pokemod
import com.cablemc.pokemod.common.advancement.PokemodCriteria
import com.cablemc.pokemod.common.advancement.criterion.CountablePokemonTypeContext
import com.cablemc.pokemod.common.advancement.criterion.trigger
import com.cablemc.pokemod.common.api.events.battles.BattleVictoryEvent
import com.cablemc.pokemod.common.api.events.pokemon.HatchEggEvent
import com.cablemc.pokemod.common.api.events.pokemon.PokemonCapturedEvent
import com.cablemc.pokemod.common.api.events.pokemon.evolution.EvolutionCompleteEvent
import com.cablemc.pokemod.common.util.getPlayer
import java.util.UUID

object AdvancementHandler {

    fun onCapture(event : PokemonCapturedEvent) {
        val playerData = Pokemod.playerData.get(event.player)
        val advancementData = playerData.advancementData
        advancementData.updateTotalCaptureCount()
        PokemodCriteria.CATCH_POKEMON.trigger(event.player, CountablePokemonTypeContext(advancementData.totalCaptureCount, "any"))
        event.pokemon.types.forEach {
            advancementData.updateTotalTypeCaptureCount(it)
            PokemodCriteria.CATCH_POKEMON.trigger(event.player, CountablePokemonTypeContext(advancementData.getTotalTypeCaptureCount(it), it.name))
        }
        if (event.pokemon.shiny) {
            advancementData.updateTotalShinyCaptureCount()
            PokemodCriteria.CATCH_SHINY_POKEMON.trigger(event.player, advancementData.totalShinyCaptureCount)
        }
        Pokemod.playerData.saveSingle(playerData)
    }

    fun onHatch(event: HatchEggEvent) {
        val playerData = Pokemod.playerData.get(event.player)
        val advancementData = playerData.advancementData
        advancementData.updateTotalEggsHatched()
        Pokemod.playerData.saveSingle(playerData)
        PokemodCriteria.EGG_HATCH.trigger(event.player, advancementData.totalEggsHatched)
    }

    fun onEvolve(event: EvolutionCompleteEvent) {
        val playerData = event.pokemon.getOwnerPlayer()?.let { Pokemod.playerData.get(it) }
        if (playerData != null) {
            val advancementData = playerData.advancementData
            advancementData.updateTotalEvolvedCount()
            Pokemod.playerData.saveSingle(playerData)
            PokemodCriteria.EVOLVE_POKEMON.trigger(event.pokemon.getOwnerPlayer()!!, advancementData.totalEvolvedCount)
        }
    }

    fun onWinBattle(event: BattleVictoryEvent) {
        if (event.battle.isPvW) {
            return
        }

        event.winners
            .flatMap { it.getPlayerUUIDs().mapNotNull(UUID::getPlayer) }
            .forEach { player ->
                val playerData = Pokemod.playerData.get(player)
                val advancementData = playerData.advancementData
                advancementData.updateTotalBattleVictoryCount()
                Pokemod.playerData.saveSingle(playerData)
                PokemodCriteria.WIN_BATTLE.trigger(player, advancementData.totalBattleVictoryCount)
            }
    }
}