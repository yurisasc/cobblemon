/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.events

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.Cobblemon.config
import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent
import com.cobblemon.mod.common.api.pokemon.experience.CaptureExperienceSource
import com.cobblemon.mod.common.api.tags.CobblemonItemTags
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon

object CaptureXpHandler {
    fun handleCaptureInBattle(event: PokemonCapturedEvent) {
        val battle = Cobblemon.battleRegistry.getBattleByParticipatingPlayer(event.player) ?: return
        val caughtBattleMonActor = battle.actors.find { it.uuid == event.pokemon.uuid } ?: return
        val caughtBattleMon = caughtBattleMonActor.pokemonList.find { it.uuid == event.pokemon.uuid } ?: return

        caughtBattleMonActor.getSide().getOppositeSide().actors.forEach { opponentActor ->
            opponentActor.pokemonList.filter {
                it.health > 0 && (caughtBattleMon.facedOpponents.contains(it) || it.effectedPokemon.heldItem()
                    .isIn(CobblemonItemTags.EXPERIENCE_SHARE))
            }.forEach { opponentMon ->
                val xpShareOnly = !caughtBattleMon.facedOpponents.contains(opponentMon)
                val xpShareOnlyModifier =
                    (if (xpShareOnly) config.experienceShareMultiplier else 1).toDouble()
                val experience = Cobblemon.experienceCalculator.calculate(
                    opponentMon, caughtBattleMon, config.captureXpMultiplier * xpShareOnlyModifier
                )
                if (experience > 0) {
                    opponentActor.awardExperience(opponentMon, experience)
                }
            }
        }
    }

    fun handleCaptureOutOfBattle(event: PokemonCapturedEvent) {
        val source = CaptureExperienceSource(event.pokemon)
        val playerParty = Cobblemon.storage.getParty(event.player)
        val first = playerParty.firstOrNull { it != event.pokemon && it.currentHealth > 0 } ?: return
        val targetMons = playerParty.filter {
            it != event.pokemon && it.currentHealth > 0 && (it.uuid == first.uuid || it.heldItem()
                .isIn(CobblemonItemTags.EXPERIENCE_SHARE))
        }
        targetMons.forEach { targetMon ->
            val xpShareOnly = targetMon.uuid != first.uuid
            val xpShareOnlyModifier = (if (xpShareOnly) config.experienceShareMultiplier else 1).toDouble()
            val experience = Cobblemon.experienceCalculator.calculate(
                BattlePokemon.safeCopyOf(targetMon),
                BattlePokemon.safeCopyOf(event.pokemon),
                config.captureXpMultiplier * xpShareOnlyModifier
            )
            targetMon.addExperienceWithPlayer(event.player, source, experience)
        }
    }
}