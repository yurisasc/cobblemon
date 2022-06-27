package com.cablemc.pokemoncobbled.common.api.pokemon.evolution

import com.cablemc.pokemoncobbled.common.pokemon.FormData
import com.cablemc.pokemoncobbled.common.pokemon.Gender
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.Species

/**
 * Represents an evolution of a [Pokemon], this is the client side counterpart of [Evolution].
 * This has no attachments to any data regarding the evolution itself and only serves for display purposes and basic communication.
 *
 * @author Licious
 * @since April 28th, 2022
 */
interface EvolutionDisplay : EvolutionLike {

    /**
     * The [Species] of the evolution result.
     */
    val species: Species

    /**
     * The aspects of the evolution result.
     * These are used by the client to resolve the entity it needs to display.
     */
    val aspects: Set<String>

}