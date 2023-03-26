package com.cobblemon.mod.common.api.events.battles

import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon

/**
 * Event fired when a [BattlePokemon] faints.
 *
 * @author Segfault Guy
 * @since March 25th, 2023
 */
data class BattleFaintedEvent (
    override val battle: PokemonBattle,
    val killed: BattlePokemon?,
    val killer: BattlePokemon?
) : BattleEvent