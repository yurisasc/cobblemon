package com.cobblemon.mod.common.battles.interpreter.instructions

import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.pokemon.status.Statuses
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.battles.dispatch.InterpreterInstruction
import com.cobblemon.mod.common.util.battleLang
import com.cobblemon.mod.common.util.lang

/**
 * Formats: |cant|POKEMON|REASON and |cant|POKEMON|REASON|MOVE
 *
 * The POKEMON could not perform a move because of the indicated REASON.
 * @author Deltric
 * @since January 22, 2022
 */
class CantInstruction(val message: BattleMessage): InterpreterInstruction {

    override fun invoke(battle: PokemonBattle) {
        battle.dispatchWaiting {
            val pokemon = message.battlePokemon(0, battle) ?: return@dispatchWaiting
            val effectID = message.effectAt(1)?.id ?: return@dispatchWaiting
            val name = pokemon.getName()
            // Move may be null as it's not always given
            val moveName = message.moveAt(2)?.displayName ?: run { "(Unrecognized: ${message.argumentAt(2)})".text() }

            val lang = when (effectID) {
                // TODO: in the games they use a generic image because there is a popup of the ability and the sprite of the mon, it may be good to have a similar system here
                "armortail", "damp", "dazzling", "queenlymajesty" -> battleLang("cant.generic", name, moveName)
                "par", "slp", "frz" -> {
                    val status = Statuses.getStatus(effectID)?.name?.path ?: return@dispatchWaiting
                    lang("status.$status.is", name)
                }
                else -> battleLang("cant.$effectID", name, moveName)
            }

            battle.broadcastChatMessage(lang.red())
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }
}