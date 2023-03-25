package com.cobblemon.mod.common.api.events.battles

import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor
import com.cobblemon.mod.common.battles.ActiveBattlePokemon
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon
import com.cobblemon.mod.common.pokemon.Pokemon

/**
 * Event fired when a [BattlePokemon] faints.
 *
 * @author Segfault Guy
 * @since March 25th, 2023
 */
data class BattleFaintedEvent (
    val battle: PokemonBattle,
    val pokemon: BattlePokemon?
)