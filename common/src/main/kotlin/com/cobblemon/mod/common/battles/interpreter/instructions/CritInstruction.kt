package com.cobblemon.mod.common.battles.interpreter.instructions

import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.text.yellow
import com.cobblemon.mod.common.battles.ShowdownInterpreter
import com.cobblemon.mod.common.battles.dispatch.InterpreterInstruction
import com.cobblemon.mod.common.pokemon.evolution.progress.LastBattleCriticalHitsEvolutionProgress
import com.cobblemon.mod.common.util.battleLang

/**
 * Format: |-crit|POKEMON
 *
 * POKEMON received a critical hit.
 * @author Hunter
 * @since August 18, 2022
 */
class CritInstruction(val message: BattleMessage): InterpreterInstruction {

    override fun invoke(battle: PokemonBattle) {
        battle.dispatchGo {
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchGo
            battle.broadcastChatMessage(battleLang("crit").yellow())
            ShowdownInterpreter.lastCauser[battle.battleId]?.let { message ->
                val battlePokemon = message.getBattlePokemon(0, battle) ?: return@let
                if (LastBattleCriticalHitsEvolutionProgress.supports(battlePokemon.effectedPokemon)) {
                    val progress = battlePokemon.effectedPokemon.evolutionProxy.current().progressFirstOrCreate({ it is LastBattleCriticalHitsEvolutionProgress }) { LastBattleCriticalHitsEvolutionProgress() }
                    progress.updateProgress(LastBattleCriticalHitsEvolutionProgress.Progress(progress.currentProgress().amount + 1))
                }
            }
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }
}