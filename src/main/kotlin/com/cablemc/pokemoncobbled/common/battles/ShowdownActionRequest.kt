package com.cablemc.pokemoncobbled.common.battles

data class ShowdownActionRequest(
    val active: List<ShowdownMoveset>
)

data class ShowdownMoveset(
    val moves: List<ShowdownMove>
)

data class ShowdownMove(
    val move: String,
    val id: String,
    val pp: Int,
    val maxpp: Int,
    val target: String,
    val disabled: Boolean
)
