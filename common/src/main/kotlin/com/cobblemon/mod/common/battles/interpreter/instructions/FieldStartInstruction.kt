package com.cobblemon.mod.common.battles.interpreter.instructions

import com.cobblemon.mod.common.api.battles.interpreter.BattleContext
import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.battles.ShowdownInterpreter
import com.cobblemon.mod.common.battles.dispatch.InterpreterInstruction
import com.cobblemon.mod.common.util.battleLang
import net.minecraft.text.Text

/**
 * Format: |-fieldstart|CONDITION
 *
 * The field CONDITION has started.
 * @author Xylopia
 * @since January 31st, 2023
 */
class FieldStartInstruction(val message: BattleMessage): InterpreterInstruction {

    override fun invoke(battle: PokemonBattle) {
        val effect = message.effectAt(0) ?: return
        val user = message.battlePokemonFromOptional(battle)
        user?.let { ShowdownInterpreter.broadcastOptionalAbility(battle, effect, user) }

        battle.dispatchWaiting(1.5F) {
            // Note persistent is a CAP ability only we can ignore the flag
            val lang = battleLang("fieldstart.${effect.id}", user?.getName() ?: Text.literal("UNKNOWN"))
            battle.broadcastChatMessage(lang)

            val type = BattleContext.Type.valueOf(effect.rawData.substringAfterLast(" ").uppercase())
            battle.contextManager.add(ShowdownInterpreter.getContextFromAction(message, type, battle))
        }
    }
}