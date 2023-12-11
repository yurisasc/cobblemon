/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.battles.interpreter.instructions

import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.moves.Moves
import com.cobblemon.mod.common.battles.ShowdownInterpreter
import com.cobblemon.mod.common.battles.dispatch.InstructionSet
import com.cobblemon.mod.common.battles.dispatch.ParentInstruction
import com.cobblemon.mod.common.pokemon.evolution.progress.UseMoveEvolutionProgress
import com.cobblemon.mod.common.util.battleLang

class MoveInstruction(
    instructionSet: InstructionSet,
    message: BattleMessage
) : ParentInstruction(instructionSet, message) {
    override fun invoke(battle: PokemonBattle) {
        val userPokemon = message.getBattlePokemon(0, battle) ?: return
        val targetPokemon = message.getBattlePokemon(2, battle)
        val effect = message.effectAt(1) ?: return
        val optionalEffect = message.effect()
        val move = Moves.getByNameOrDummy(effect.id)
        val pokemonName = userPokemon.getName()
        ShowdownInterpreter.broadcastOptionalAbility(battle, optionalEffect, pokemonName)


        battle.dispatchGo {
            ShowdownInterpreter.lastCauser[battle.battleId] = message

            userPokemon.effectedPokemon.let { pokemon ->
                if (UseMoveEvolutionProgress.supports(pokemon, move)) {
                    val progress = pokemon.evolutionProxy.current().progressFirstOrCreate({ it is UseMoveEvolutionProgress && it.currentProgress().move == move }) { UseMoveEvolutionProgress() }
                    progress.updateProgress(UseMoveEvolutionProgress.Progress(move, progress.currentProgress().amount + 1))
                }
            }

            val lang = when {
                optionalEffect?.id == "magicbounce" ->
                    battleLang("ability.magicbounce", pokemonName, move.displayName)
                move.name != "struggle" && targetPokemon != null && targetPokemon != userPokemon ->
                    battleLang("used_move_on", pokemonName, move.displayName, targetPokemon.getName())
                else ->
                    battleLang("used_move", pokemonName, move.displayName)
            }
            battle.broadcastChatMessage(lang)

            battle.majorBattleActions[userPokemon.uuid] = message
        }
    }
}