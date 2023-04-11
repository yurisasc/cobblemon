package com.cobblemon.mod.common.api.events.battles

import com.cobblemon.mod.common.api.battles.interpreter.BattleContext
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon

/**
 * Event fired when a [BattlePokemon] faints. Exposes the [BattlePokemon] that fainted and the [BattleContext]
 * of how it fainted.
 *
 * @author Segfault Guy
 * @since April 6th, 2023
 */
data class BattleFaintedEvent (
        override val battle: PokemonBattle,
        val killed: BattlePokemon,
        val context: BattleContext
) : BattleEvent
