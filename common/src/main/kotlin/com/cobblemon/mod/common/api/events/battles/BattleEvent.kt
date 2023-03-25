package com.cobblemon.mod.common.api.events.battles

import com.cobblemon.mod.common.api.battles.model.PokemonBattle

/**
 * Interface for events involving the Showdown battle engine
 *
 * @author Segfault Guy
 * @since March 25th 2023
 */
interface BattleEvent {

    /**
     * The [PokemonBattle] that is the subject of the event.
     */
    val battle: PokemonBattle

}