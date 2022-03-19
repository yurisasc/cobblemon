package com.cablemc.pokemoncobbled.common.battles

import java.util.UUID

data class ShowdownActionRequest(
    var wait: Boolean = false,
    val active: List<ShowdownMoveset>? = null,
    val forceSwitch: List<Boolean> = emptyList(),
    val noCancel: Boolean = false,
    val side: ShowdownSide? = null
)

data class ShowdownMoveset(
    val moves: List<InBattleMove>
)

data class ShowdownSide(
    val name: UUID,
    val id: String,
    val pokemon: List<ShowdownPokemon>
)

data class ShowdownPokemon(
    val ident: String,
    val details: String,
    val condition: String,
    val active: Boolean,
    val moves: List<String>,
    val baseAbility: String,
    val pokeball: String,
    val ability: String
)