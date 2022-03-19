package com.cablemc.pokemoncobbled.common.battles

import com.cablemc.pokemoncobbled.common.api.battles.model.PokemonBattle
import com.cablemc.pokemoncobbled.common.api.battles.model.actor.BattleActor

/**
 * Unlike the Showdown side.ts, this can represent multiple actors.
 *
 * @author Hiroku
 * @since March 9th, 2022
 */
class BattleSide(vararg val actors: BattleActor) {
    val activePokemon: List<ActiveBattlePokemon>
        get() = actors.flatMap { it.activePokemon }

    lateinit var battle: PokemonBattle
    fun getOppositeSide() = if (this == battle.side1) battle.side2 else battle.side1
}