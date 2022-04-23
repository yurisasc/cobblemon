package com.cablemc.pokemoncobbled.common.api.events.pokemon

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

/**
 * Event that is fired when a Pokémon has fainted
 */
data class PokemonFaintedEvent(
    val pokemon: Pokemon,
    var faintedTimer: Int
)
