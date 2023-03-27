package com.cobblemon.mod.common.api.events.battles

import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon

/**
 * Event fired when a [BattlePokemon] faints.
 *
 * Users should note that killer is null when a Pokemon faints passively (ex: weather, status, entry hazards,
 * perish song), and killer is the same as killed when a Pokemon directly causes itself ot faint (ex: recoil,
 * confusion, self-destructive moves)
 *
 * @author Segfault Guy
 * @since March 25th, 2023
 */
data class BattleFaintedEvent (
    override val battle: PokemonBattle,
    val killed: BattlePokemon,
    val killer: BattlePokemon?
) : BattleEvent