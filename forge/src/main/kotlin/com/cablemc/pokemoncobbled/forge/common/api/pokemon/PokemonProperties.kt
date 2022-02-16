package com.cablemc.pokemoncobbled.forge.common.api.pokemon

import com.cablemc.pokemoncobbled.forge.common.pokemon.Gender

/** A grouping of typical, chooseable properties for a Pokémon. Species agnostic. */
class PokemonProperties {
    var gender: Gender? = null
    var level: Int? = null

}