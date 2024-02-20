package com.cobblemon.mod.common.battles.interpreter.instructions

import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.battles.dispatch.InterpreterInstruction
import com.cobblemon.mod.common.util.battleLang

/**
 * Format: |-mustrecharge|POKEMON
 *
 * POKEMON must spend the turn recharging from a previous move.
 * @author Hunter
 * @since September 25, 2022
 */
class RechargeInstruction(val message: BattleMessage): InterpreterInstruction {

    override fun invoke(battle: PokemonBattle) {
        battle.dispatchWaiting(2F){
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchWaiting
            battle.broadcastChatMessage(battleLang("recharge", pokemon.getName()))
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }
}