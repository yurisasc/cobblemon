package com.cobblemon.mod.common.battles.interpreter.instructions

import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.battles.dispatch.InterpreterInstruction
import com.cobblemon.mod.common.entity.pokemon.effects.TransformEffect
import com.cobblemon.mod.common.util.battleLang

/**
 * Format: |-transform|POKEMON|POKEMON
 *
 * POKEMON used Transform to turn into target POKEMON.
 * @author jeffw773
 * @since November 28, 2023
 */
class TransformInstruction(val message: BattleMessage): InterpreterInstruction {

    override fun invoke(battle: PokemonBattle) {
        battle.dispatchWaiting(1.5F) {
            val pokemon = message.battlePokemon(0, battle) ?: return@dispatchWaiting
            val pokemonName = pokemon.getName()
            val targetPokemon = message.battlePokemon(1, battle) ?: return@dispatchWaiting
            val targetPokemonName = targetPokemon.getName()

            pokemon.entity?.let { TransformEffect(targetPokemon.effectedPokemon).start(it) }

            val lang = battleLang("transform", pokemonName, targetPokemonName)
            battle.broadcastChatMessage(lang)
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }
}