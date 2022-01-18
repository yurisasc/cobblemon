package com.cablemc.pokemoncobbled.common.battles.ai

import com.cablemc.pokemoncobbled.common.api.battles.model.Battle
import com.cablemc.pokemoncobbled.common.api.battles.model.ai.ArtificialDecider
import com.cablemc.pokemoncobbled.common.api.battles.model.subject.AIBattleSubject
import com.cablemc.pokemoncobbled.common.api.battles.model.subject.BattleSubject
import com.cablemc.pokemoncobbled.common.api.moves.Move

/**
 * Artificial decider that makes random choices for moves
 *
 * @since January 16th, 2022
 * @author Deltric
 */
class RandomArtificialDecider : ArtificialDecider {

    override fun chooseMove(battle: Battle, subject: AIBattleSubject, opponents: List<BattleSubject>): Move {
        TODO("Not yet implemented")
    }

}