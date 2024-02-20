package com.cobblemon.mod.common.battles.interpreter.instructions

import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.battles.dispatch.InterpreterInstruction
import com.cobblemon.mod.common.util.battleLang

/**
 * Format: |detailschange|POKEMON|DETAILS|HP STATUS
 *
 * POKEMON has changed formes permanently (i.e. Mega Evolution) to DETAILS.
 * @author Segfault Guy
 * @since September 10, 2023
 */
class DetailsChangeInstruction(val message: BattleMessage): InterpreterInstruction {

    override fun invoke(battle: PokemonBattle) {
        val battlePokemon = message.getBattlePokemon(0, battle) ?: return
        val pokemonName = battlePokemon.getName()
        val formName = message.argumentAt(1)?.split(',')?.get(0)?.substringAfter('-')?.lowercase() ?: return
        battle.dispatchWaiting {
            battle.broadcastChatMessage(battleLang("detailschange.$formName", pokemonName))
            battle.majorBattleActions[battlePokemon.uuid] = message
        }
    }
}