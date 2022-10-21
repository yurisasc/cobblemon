/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.pokemon

import com.cablemc.pokemod.common.pokemon.Pokemon

/**
 * A simple functional interface for extracting a property from a [Pokemon] and putting it into a [PokemonProperties].
 *
 * This mainly exists for the purposes of [Pokemon.createPokemonProperties] specifically.
 *
 * @author Hiroku
 * @since May 12th, 2022
 */
fun interface PokemonPropertyExtractor {
    companion object {
        val SPECIES = PokemonPropertyExtractor { pokemon, properties -> properties.species = pokemon.species.resourceIdentifier.toString() }
        val SHINY = PokemonPropertyExtractor { pokemon, properties -> properties.shiny = pokemon.shiny }
        val ASPECTS = PokemonPropertyExtractor { pokemon, properties -> properties.aspects = pokemon.aspects }
        val LEVEL = PokemonPropertyExtractor { pokemon, properties -> properties.level = pokemon.level }
        val GENDER = PokemonPropertyExtractor { pokemon, properties -> properties.gender = pokemon.gender }
    }

    operator fun invoke(pokemon: Pokemon, properties: PokemonProperties)
}
