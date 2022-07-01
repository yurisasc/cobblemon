package com.cablemc.pokemoncobbled.common.api.pokemon.evolution

/**
 * Represents the base of an evolution.
 * It can either be the full data container present on the server side [Evolution].
 * Or the client side representation for display [EvolutionDisplay].
 *
 * @author Licious
 * @since April 28th, 2022
 */
interface EvolutionLike {

    /**
     * The unique id of the evolution.
     * It should be human-readable, I.E pikachu_level
     */
    val id: String

}