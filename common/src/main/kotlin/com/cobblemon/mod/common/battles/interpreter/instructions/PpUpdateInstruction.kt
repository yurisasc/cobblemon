package com.cobblemon.mod.common.battles.interpreter.instructions

import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.battles.dispatch.GO
import com.cobblemon.mod.common.battles.dispatch.InterpreterInstruction

/**
 * Format: |pp_update|<side_id>: <pokemon_uuid>|...<move_id>: <move_pp>
 *
 * @author Licious
 * @since September 22, 2022
 */
class PpUpdateInstruction( val message: BattleMessage): InterpreterInstruction {

    override fun invoke(battle: PokemonBattle) {
        battle.dispatch {
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatch GO
            val moveDatum = message.argumentAt(1)?.split(", ") ?: return@dispatch GO
            moveDatum.forEach { moveData ->
                val moveIdAndPp = moveData.split(": ")
                val moveId = moveIdAndPp[0]
                val movePp = moveIdAndPp[1]
                val move = pokemon.effectedPokemon.moveSet.firstOrNull { move -> move.name.equals(moveId, true) } ?: return@dispatch GO
                move.currentPp = movePp.toInt()
            }
            GO
        }
    }
}