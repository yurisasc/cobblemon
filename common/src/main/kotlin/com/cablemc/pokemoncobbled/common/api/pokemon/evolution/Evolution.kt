package com.cablemc.pokemoncobbled.common.api.pokemon.evolution

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

interface Evolution {

    /**
     * The unique id of this [Evolution].
     * It should be human readable, I.E pikachu_level
     */
    val id: String

    /**
     * The result of this evolution.
     */
    val result: PokemonProperties

    /**
     * If this evolution will allow the user to choose when to start it or not.
     */
    val optional: Boolean

    // ToDo pending held items.
    /**
     * If this [Evolution] will consume the [Pokemon.heldItem]
     */
    val consumeHeldItem: Boolean

    /**
     * The [EvolutionRequirement]s behind this evolution.
     */
    val requirements: List<EvolutionRequirement>

    /**
     * Checks if the given [Pokemon] passes all the conditions and is ready to evolve.
     *
     * @param pokemon The [Pokemon] being queried.
     * @return If the [Evolution] can start.
     */
    fun test(pokemon: Pokemon) = this.requirements.all { requirement -> requirement.check(pokemon) }

    fun evolve(pokemon: Pokemon) {
        this.result.apply(pokemon)
        // ToDo actually queue the client if needed or start the proper animation
    }

}