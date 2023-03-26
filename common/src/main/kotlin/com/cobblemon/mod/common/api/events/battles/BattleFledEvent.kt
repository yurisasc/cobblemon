package com.cobblemon.mod.common.api.events.battles

import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor

/**
 * Event fired when a [PokemonBattle] is fled by a [BattleActor]. Canceling this event prevents players
 * from fleeing battles
 *
 * @author Segfault Guy
 * @since March 25th 2023
 */
class BattleFledEvent (

    override val battle: PokemonBattle

) : BattleEvent