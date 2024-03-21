package com.cobblemon.mod.common.battles.interpreter.instructions

import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.battles.dispatch.InterpreterInstruction
import com.cobblemon.mod.common.util.battleLang

/**
 * Format: |-block|POKEMON|EFFECT|MOVE|ATTACKER|(of)SOURCE
 *
 * An effect targeted at POKEMON was blocked by EFFECT. This may optionally specify that the effect was a MOVE from ATTACKER.
 *
 * An optional SOURCE will note the owner of the EFFECT, in the case that it's not EFFECT (for instance, an ally with Aroma Veil.)
 * @author jeffw773
 * @since November 6, 2023
 */
class BlockInstruction(val message: BattleMessage): InterpreterInstruction {

    override fun invoke(battle: PokemonBattle) {
        battle.dispatchWaiting(1.5F) {
            val pokemon = message.battlePokemon(0, battle) ?: return@dispatchWaiting
            val pokemonName = pokemon.getName()
            val effectID = message.effectAt(1)?.id ?: return@dispatchWaiting
            val lang = battleLang("block.$effectID", pokemonName)

            battle.broadcastChatMessage(lang)
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }
}