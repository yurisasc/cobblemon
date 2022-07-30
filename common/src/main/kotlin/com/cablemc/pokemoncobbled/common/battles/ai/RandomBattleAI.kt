package com.cablemc.pokemoncobbled.common.battles.ai

import com.cablemc.pokemoncobbled.common.api.battles.model.ai.BattleAI
import com.cablemc.pokemoncobbled.common.battles.*

/**
 * AI that randomly chooses a move from its moveset at a random target.
 *
 * @since January 16th, 2022
 * @author Deltric, Hiroku
 */
class RandomBattleAI : BattleAI {
    override fun choose(
        activeBattlePokemon: ActiveBattlePokemon,
        moveset: ShowdownMoveset?,
        forceSwitch: Boolean
    ): ShowdownActionResponse {
        if (forceSwitch || activeBattlePokemon.isGone()) {
            val switchTo = activeBattlePokemon.actor.pokemonList.filter { it.canBeSentOut() }.randomOrNull()
                ?: return DefaultActionResponse() //throw IllegalStateException("Need to switch but no Pokémon to switch to")
            switchTo.willBeSwitchedIn = true
            return SwitchActionResponse(switchTo.uuid)
        }

        if (moveset == null) {
            return PassActionResponse
        }
        val move = moveset.moves
            .filter { it.canBeUsed() }
            .filter { it.mustBeUsed() || it.target.targetList(activeBattlePokemon)?.isEmpty() != true }
            .randomOrNull()
            ?: return MoveActionResponse("struggle")

        val target = if (move.mustBeUsed()) null else move.target.targetList(activeBattlePokemon)
        return if (target == null) {
            MoveActionResponse(move.id)
        } else {
            // prioritize opponents rather than allies
            val chosenTarget = target.filter { !it.isAllied(activeBattlePokemon) }.randomOrNull() ?: target.random()
            MoveActionResponse(move.id, (chosenTarget as ActiveBattlePokemon).getPNX())
        }
    }
}