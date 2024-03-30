package com.cobblemon.mod.common.battles.interpreter.instructions

import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.battles.dispatch.InterpreterInstruction
import com.cobblemon.mod.common.util.battleLang
import net.minecraft.text.Text

/**
 * Format: |-singleturn|POKEMON|MOVE
 *
 * POKEMON used MOVE which causes a temporary effect lasting the duration of the turn.
 * @author Renaissance
 * @since March 24th, 2023
 */
class SingleTurnInstruction(val message: BattleMessage): InterpreterInstruction {

    override fun invoke(battle: PokemonBattle) {
        battle.dispatchWaiting(1.5F) {
            val pokemon = message.battlePokemon(0, battle) ?: return@dispatchWaiting
            val pokemonName = pokemon.getName()
            val sourceName = message.battlePokemonFromOptional(battle)?.getName() ?: Text.literal("UNKOWN")
            val effectID = message.effectAt(1)?.id ?: return@dispatchWaiting
            val lang = battleLang("singleturn.$effectID", pokemonName, sourceName)
            battle.broadcastChatMessage(lang)
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }
}