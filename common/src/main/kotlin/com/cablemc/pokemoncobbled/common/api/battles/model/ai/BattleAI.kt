package com.cablemc.pokemoncobbled.common.api.battles.model.ai

import com.cablemc.pokemoncobbled.common.api.battles.model.PokemonBattle
import com.cablemc.pokemoncobbled.common.api.battles.model.actor.AIBattleActor
import com.cablemc.pokemoncobbled.common.api.battles.model.actor.BattleActor
import com.cablemc.pokemoncobbled.common.api.moves.Move

/**
 * Interface for an actors battle AI
 *
 * @since January 16th, 2022
 * @author Deltric
 */
interface BattleAI {
    /**
     * Requests that the AI choose a move
     * @return the move choice
     */
    fun chooseMove(battle: PokemonBattle, actor: AIBattleActor, opponents: List<BattleActor>) : Move
}