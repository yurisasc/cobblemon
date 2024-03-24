package com.cobblemon.mod.common.battles.interpreter.instructions

import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.battles.dispatch.InterpreterInstruction
import com.cobblemon.mod.common.util.battleLang

class EffectivenessInstruction(
    val battle: PokemonBattle,
    val message: BattleMessage,
    val typeOfEffectiveness: String
) : InterpreterInstruction {

    /**
     * Format:
     * |-supereffective|p%a
     *
     * player % was weak against the attack.
     */
    private fun handleSuperEffectiveInstruction(
        battle: PokemonBattle,
        message: BattleMessage
    ) {
        battle.dispatchGo {
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchGo
            battle.broadcastChatMessage(battleLang("superEffective"))
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    /**
     * Format:
     * |-resisted|p%a
     *
     * player % resisted the attack.
     */
    private fun handleResistInstruction(
        battle: PokemonBattle,
        message: BattleMessage
    ) {
        battle.dispatchGo {
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchGo
            battle.broadcastChatMessage(battleLang("resisted"))
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    /**
     * Format:
     * |-immune|POKEMON
     *
     * The POKEMON was immune to a move.
     */
    private fun handleImmuneInstruction(battle: PokemonBattle, message: BattleMessage) {
        battle.dispatchWaiting {
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchWaiting
            val name = pokemon.getName()
            battle.broadcastChatMessage(battleLang("immune", name).red())
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }


    // Depending on the start of the raw message of the BattleMessage, we can determine the type of instruction
    override fun invoke(battle: PokemonBattle) {
        when (typeOfEffectiveness) {
            "supereffective" -> handleSuperEffectiveInstruction(battle, message)
            "resisted" -> handleResistInstruction(battle, message)
            "immune" -> handleImmuneInstruction(battle, message)
        }

    }


}
