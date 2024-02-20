package com.cobblemon.mod.common.battles.interpreter.instructions

import com.cobblemon.mod.common.api.battles.interpreter.BattleContext
import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.battles.dispatch.InterpreterInstruction
import com.cobblemon.mod.common.util.battleLang

/**
 * Format: |-clearnegativeboost|POKEMON
 *
 * Clear the negative boosts from the target POKEMON (usually as the result of a zeffect).
 * @author Segfault Guy
 * @since September 10, 2023
 */
class ClearNegativeBoostInstruction(val message: BattleMessage): InterpreterInstruction {

    override fun invoke(battle: PokemonBattle) {
        val battlePokemon = message.getBattlePokemon(0, battle) ?: return
        val pokemonName = battlePokemon.getName()
        battle.dispatchWaiting(1.5F) {
            val lang = when {
                message.hasOptionalArgument("zeffect") -> battleLang("clearallnegativeboost.zeffect", pokemonName)
                else -> battleLang("clearallnegativeboost", pokemonName)
            }
            if (!message.hasOptionalArgument("silent")) {
                battle.broadcastChatMessage(lang)
            }

            battlePokemon.contextManager.clear(BattleContext.Type.UNBOOST)
            battle.minorBattleActions[battlePokemon.uuid] = message
        }
    }
}