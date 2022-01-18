package com.cablemc.pokemoncobbled.common.api.battles.model.ai

import com.cablemc.pokemoncobbled.common.api.battles.model.Battle
import com.cablemc.pokemoncobbled.common.api.battles.model.subject.AIBattleSubject
import com.cablemc.pokemoncobbled.common.api.battles.model.subject.BattleSubject
import com.cablemc.pokemoncobbled.common.api.moves.Move

/**
 * Interface for an artificial intelligence decision maker
 *
 * @since January 16th, 2022
 * @author Deltric
 */
interface ArtificialDecider {
    /**
     * Makes a decision on what move to pick
     * @return the move choice
     */
    fun chooseMove(battle: Battle, subject: AIBattleSubject, opponents: List<BattleSubject>) : Move
}