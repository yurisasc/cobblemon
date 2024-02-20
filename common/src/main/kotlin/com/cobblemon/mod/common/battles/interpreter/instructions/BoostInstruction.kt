package com.cobblemon.mod.common.battles.interpreter.instructions

import com.cobblemon.mod.common.api.battles.interpreter.BattleContext
import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.battles.ShowdownInterpreter
import com.cobblemon.mod.common.battles.dispatch.InstructionSet
import com.cobblemon.mod.common.battles.dispatch.InterpreterInstruction
import com.cobblemon.mod.common.util.battleLang

/**
 * Format: |-boost|POKEMON|STAT|AMOUNT or |-unboost|POKEMON|STAT|AMOUNT
 *
 * POKEMON has gained or lost AMOUNT in STAT, using the standard rules for stat changes in-battle.
 * STAT is a standard three-letter abbreviation fot the stat in question.
 * @author Hiroku
 * @since August 20, 2022
 */
class BoostInstruction(val instructionSet: InstructionSet, val message: BattleMessage, val remainingLines: Iterator<BattleMessage>, val isBoost: Boolean = true): InterpreterInstruction {

    override fun invoke(battle: PokemonBattle) {
        val pokemon = message.getBattlePokemon(0, battle) ?: return
        val statKey = message.argumentAt(1) ?: return
        val stages = message.argumentAt(2)?.toInt() ?: return
        val stat = Stats.getStat(statKey).displayName
        val severity = Stats.getSeverity(stages)
        val rootKey = if (isBoost) "boost" else "unboost"

        battle.dispatchWaiting(1.5F) {
            val lang = when {
                message.hasOptionalArgument("zeffect") -> battleLang("$rootKey.$severity.zeffect", pokemon.getName(), stat)
                else -> battleLang("$rootKey.$severity", pokemon.getName(), stat)
            }
            battle.broadcastChatMessage(lang)

            val boostBucket = if (isBoost) BattleContext.Type.BOOST else BattleContext.Type.UNBOOST
            val context = ShowdownInterpreter.getContextFromAction(message, boostBucket, battle)
            // TODO: replace with context that tracks detailed information such as # of stages
            repeat(stages) { pokemon.contextManager.add(context) }
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }
}