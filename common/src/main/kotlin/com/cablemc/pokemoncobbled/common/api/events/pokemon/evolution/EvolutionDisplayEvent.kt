package com.cablemc.pokemoncobbled.common.api.events.pokemon.evolution

import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.Evolution
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.EvolutionDisplay
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

/**
 * Fired before an evolution is sent to clients.
 * Any modification made to the [pokemon] will reflect in their display.
 * For modifications on the actual output when they accept it modify the [Evolution.result].
 *
 * @param pokemon The [Pokemon] that will evolve if this proposition is accepted.
 * @param display The current [EvolutionDisplay] the client will see.
 * @param evolution The [Evolution] being used.
 *
 * @author Licious
 * @since May 3rd, 2022
 */
data class EvolutionDisplayEvent(
    val pokemon: Pokemon,
    var display: EvolutionDisplay,
    val evolution: Evolution
)