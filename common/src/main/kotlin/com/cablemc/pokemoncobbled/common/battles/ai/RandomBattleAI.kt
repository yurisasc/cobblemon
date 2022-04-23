package com.cablemc.pokemoncobbled.common.battles.ai

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.battles.model.ai.BattleAI
import com.cablemc.pokemoncobbled.common.battles.ActiveBattlePokemon
import java.util.UUID

/**
 * AI that randomly chooses a move from its moveset at a random target.
 *
 * @since January 16th, 2022
 * @author Deltric, Hiroku
 */
class RandomBattleAI : BattleAI {
    override fun chooseMoves(activePokemon: Iterable<ActiveBattlePokemon>): Iterable<String> {
        val decisions = mutableListOf<String>()
        for (pokemon in activePokemon) {
            val move = pokemon.selectableMoves.filter { it.canBeUsed() }.randomOrNull()
            if (move == null) {
                decisions.add("move struggle")
                continue
            }

            val moveIndex = pokemon.selectableMoves.indexOf(move) + 1
            val target = move.target.targetList(pokemon)
            if (target == null) {
                decisions.add("move $moveIndex")
            } else if (target.isEmpty()) {
                decisions.add("pass")
                PokemonCobbled.LOGGER.error("Unable to find targets for ${move.move}. Weird.")
            } else {
                // prioritize opponents
                val chosenTarget = target.filter { !it.isAllied(pokemon) }.randomOrNull() ?: target.random()

                decisions.add("move $moveIndex ${chosenTarget.getSignedDigitRelativeTo(pokemon)}")
            }
        }
        return decisions
    }

    override fun chooseSwitches(activePokemon: Iterable<ActiveBattlePokemon>): Iterable<UUID> {
        val switches = mutableListOf<UUID>()
        for (pokemon in activePokemon) {
            val switchTo = pokemon.actor.pokemonList.filter { it.canBeSentOut() }.randomOrNull()
                ?: break //throw IllegalStateException("Need to switch but no Pokémon to switch to")
            switchTo.willBeSwitchedIn = true
            switches.add(switchTo.uuid)
        }
        return switches
    }
}