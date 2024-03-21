package com.cobblemon.mod.common.battles.interpreter.instructions

import com.cobblemon.mod.common.api.battles.interpreter.BattleContext
import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.battles.ShowdownInterpreter
import com.cobblemon.mod.common.battles.dispatch.InterpreterInstruction
import net.minecraft.text.Text

/**
 * Format: |-item|POKEMON|ITEM|(from)EFFECT
 *
 * ITEM held by POKEMON has been changed or revealed due to a move or ability EFFECT.
 *
 * Alt format: |-item|POKEMON|ITEM
 *
 * POKEMON has just switched in, and its ITEM is being announced to have a long-term effect.
 * @author Licious
 * @since December 30, 2022
 */
class ItemInstruction(val message: BattleMessage): InterpreterInstruction {

    override fun invoke(battle: PokemonBattle) {
        val sourceName = message.battlePokemonFromOptional(battle)?.getName() ?: Text.literal("UNKOWN")
        ShowdownInterpreter.broadcastOptionalAbility(battle, message.effect(), sourceName)

        battle.dispatchGo {
            val battlePokemon = message.battlePokemon(0, battle) ?: return@dispatchGo
            battlePokemon.heldItemManager.handleStartInstruction(battlePokemon, battle, message)
            battle.minorBattleActions[battlePokemon.uuid] = message
            battlePokemon.contextManager.add(ShowdownInterpreter.getContextFromAction(message, BattleContext.Type.ITEM, battle))
        }
    }
}