package com.cablemc.pokemoncobbled.common.battles.dispatch

import com.cablemc.pokemoncobbled.common.api.battles.model.PokemonBattle

fun interface BattleDispatch {
    operator fun invoke(battle: PokemonBattle): DispatchResult
}