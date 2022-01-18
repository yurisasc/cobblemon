package com.cablemc.pokemoncobbled.common.api.battles.model

import com.cablemc.pokemoncobbled.common.api.battles.model.subject.BattleSubject
import java.util.*

/**
 * Individual battle instance
 *
 * @since January 16th, 2022
 * @author Deltric
 */
class Battle(
    val subjects: List<BattleSubject>
) {

    val battleId: UUID = UUID.randomUUID()
    val format: String = "gen7ou"

    /**
     * Gets a subject by their showdown id
     * @return the subject if found otherwise null
     */
    fun getSubject(showdownId: String) : BattleSubject? {
        return subjects.find { subject -> subject.showdownId == showdownId }
    }

    /**
     * Gets a subject by their game id
     * @return the subject if found otherwise null
     */
    fun getSubject(gameId: UUID) : BattleSubject? {
        return subjects.find { subject -> subject.gameId == gameId }
    }
}