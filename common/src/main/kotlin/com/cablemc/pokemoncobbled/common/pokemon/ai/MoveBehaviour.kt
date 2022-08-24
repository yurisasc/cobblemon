package com.cablemc.pokemoncobbled.common.pokemon.ai

/**
 * Behavioural properties relating to a Pokémon's ability to look and move.
 *
 * @author Hiroku
 * @since July 30th, 2022
 */
class MoveBehaviour {
    val walk = WalkBehaviour()
    val swim = SwimBehaviour()
    val fly = FlyBehaviour()
    val canLook = true
}