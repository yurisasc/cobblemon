package com.cablemc.pokemoncobbled.common.api.events.pokemon.evolution

import com.cablemc.pokemoncobbled.common.api.events.Cancelable
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.Evolution
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

/**
 * Fired when an evolution is accepted.
 * Canceling will not notify users nor remove the evolution from the pending list.
 *
 * @param pokemon The [Pokemon] about to evolve.
 * @param evolution The [Evolution] being used, if
 *
 * @author Licious
 * @since April 28th, 2022
 */
data class EvolutionAcceptedEvent(
    val pokemon: Pokemon,
    val evolution: Evolution
) : Cancelable()