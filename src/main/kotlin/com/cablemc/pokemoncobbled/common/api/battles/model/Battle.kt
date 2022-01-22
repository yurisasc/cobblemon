package com.cablemc.pokemoncobbled.common.api.battles.model

import com.cablemc.pokemoncobbled.common.api.battles.model.actor.BattleActor
import java.util.*

/**
 * Individual battle instance
 *
 * @since January 16th, 2022
 * @author Deltric
 */
class Battle(
    val actors: List<BattleActor>
) {

    val battleId: UUID = UUID.randomUUID()
    val format: String = "gen7ou"

    /**
     * Gets an actor by their showdown id
     * @return the actor if found otherwise null
     */
    fun getActor(showdownId: String) : BattleActor? {
        return actors.find { actor -> actor.showdownId == showdownId }
    }

    /**
     * Gets an actor by their game id
     * @return the actor if found otherwise null
     */
    fun getActor(actorId: UUID) : BattleActor? {
        return actors.find { actor -> actor.gameId == actorId }
    }
}