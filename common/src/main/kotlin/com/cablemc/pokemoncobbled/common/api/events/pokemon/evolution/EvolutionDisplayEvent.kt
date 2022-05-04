package com.cablemc.pokemoncobbled.common.api.events.pokemon.evolution

import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.Evolution
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

/**
 * Fired before an evolution is sent to clients.
 * Any modification made to the [pokemon] will reflect in their display.
 * For modifications on the actual output when they accept it modify the [Evolution.result].
 *
 * @param pokemon The [Pokemon] that will be used for display purposes.
 * @param evolution The [Evolution] being used.
 *
 * @author Licious
 * @since May 3rd, 2022
 */
data class EvolutionDisplayEvent(
    val pokemon: Pokemon,
    val evolution: Evolution
)