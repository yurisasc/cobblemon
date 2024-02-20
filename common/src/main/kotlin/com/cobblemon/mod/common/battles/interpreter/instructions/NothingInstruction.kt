package com.cobblemon.mod.common.battles.interpreter.instructions

import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.battles.dispatch.InterpreterInstruction
import com.cobblemon.mod.common.util.battleLang

/**
 * Format: |-nothing
 *
 * Pathetic. Your move did absolutely nothing. You should feel bad for being bad.
 * @author Hiroku
 * @since August 20, 2022
 */
class NothingInstruction() : InterpreterInstruction {

    override fun invoke(battle: PokemonBattle) {
        battle.dispatchGo { battle.broadcastChatMessage(battleLang("nothing")) }
    }
}