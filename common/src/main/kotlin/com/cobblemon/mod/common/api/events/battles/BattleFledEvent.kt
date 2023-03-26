package com.cobblemon.mod.common.api.events.battles

import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor

/**
 * Event fired when a [PokemonBattle] is fled by a [PlayerBattleActor].
 *
 * @author Segfault Guy
 * @since March 25th 2023
 */
class BattleFledEvent (

    override val battle: PokemonBattle,
    val player: PlayerBattleActor

) : BattleEvent