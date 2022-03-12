package com.cablemc.pokemoncobbled.common.battles

data class ShowdownActionRequest(
    var wait: Boolean = false,
    val active: List<ShowdownMoveset>
)

data class ShowdownMoveset(
    val moves: List<InBattleMove>
)
