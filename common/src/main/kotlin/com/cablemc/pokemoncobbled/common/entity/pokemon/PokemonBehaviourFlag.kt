package com.cablemc.pokemoncobbled.common.entity.pokemon

/**
 * A short list of true/false properties that can be set on a Pokémon entity. These are
 * for use in some poses and AI cases.
 *
 * This list must not get more than 7 elements! Not without upgrading the flag these are
 * stored in from Byte to something larger.
 *
 * @author Hiroku
 * @since December 16th, 2021
 */
enum class PokemonBehaviourFlag {
    RESTING,
    LOOKING,
    EXCITED;

    val bit: Int = ordinal + 1
}