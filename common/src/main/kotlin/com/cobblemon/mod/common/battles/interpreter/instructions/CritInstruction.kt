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
import com.cobblemon.mod.common.api.text.yellow
import com.cobblemon.mod.common.battles.dispatch.InstructionSet
import com.cobblemon.mod.common.battles.dispatch.InterpreterInstruction
import com.cobblemon.mod.common.util.battleLang
import com.cobblemon.mod.common.pokemon.evolution.progress.LastBattleCriticalHitsEvolutionProgress

/**
 * Format:
 * |-crit|p%a
 *
 * player % received a critical hit.
 */
class CritInstruction(
    val battle: PokemonBattle,
    val instructionSet: InstructionSet,
    val publicMessage: BattleMessage,
) : InterpreterInstruction {
    val battlePokemon = publicMessage.getBattlePokemon(0, battle)

    override fun invoke(battle: PokemonBattle) {
        battlePokemon ?: return
        val lastCauser  = instructionSet.getMostRecentCauser(comparedTo = this)

        battle.dispatchGo {
            if(lastCauser is MoveInstruction) {
                if(LastBattleCriticalHitsEvolutionProgress.supports(lastCauser.userPokemon.effectedPokemon)) {
                    val progress = lastCauser.userPokemon.effectedPokemon.evolutionProxy.current().progressFirstOrCreate({ it is LastBattleCriticalHitsEvolutionProgress }) { LastBattleCriticalHitsEvolutionProgress() }
                    progress.updateProgress(LastBattleCriticalHitsEvolutionProgress.Progress(progress.currentProgress().amount + 1))
                }
                if(lastCauser.spreadTargets.isNotEmpty()) {
                    val pokemonName = battlePokemon.getName()
                    battle.broadcastChatMessage(battleLang("crit_spread", pokemonName).yellow())
                } else {
                    battle.broadcastChatMessage(battleLang("crit"))
                }
            }
            battle.minorBattleActions[battlePokemon.uuid] = publicMessage
        }
    }
}