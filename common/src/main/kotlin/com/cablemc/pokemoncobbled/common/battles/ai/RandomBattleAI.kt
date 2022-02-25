package com.cablemc.pokemoncobbled.common.battles.ai

import com.cablemc.pokemoncobbled.common.api.battles.model.PokemonBattle
import com.cablemc.pokemoncobbled.common.api.battles.model.ai.BattleAI
import com.cablemc.pokemoncobbled.common.api.battles.model.actor.AIBattleActor
import com.cablemc.pokemoncobbled.common.api.battles.model.actor.BattleActor
import com.cablemc.pokemoncobbled.common.api.moves.Move

/**
 * AI that randomly chooses a move from its moveset
 *
 * @since January 16th, 2022
 * @author Deltric
 */
class RandomBattleAI : BattleAI {

    override fun chooseMove(battle: PokemonBattle, actor: AIBattleActor, opponents: List<BattleActor>): Move {
        TODO("Not yet implemented")
    }

}