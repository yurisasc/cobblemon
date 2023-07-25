/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokemon

import com.cobblemon.mod.common.pokemon.Pokemon

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
        @JvmField
        val SPECIES = PokemonPropertyExtractor { pokemon, properties -> properties.species = pokemon.species.resourceIdentifier.toString() }
        @JvmField
        val FORM = PokemonPropertyExtractor { pokemon, properties -> properties.form = pokemon.form.formOnlyShowdownId() }
        @JvmField
        val SHINY = PokemonPropertyExtractor { pokemon, properties -> properties.shiny = pokemon.shiny }
        @JvmField
        val ASPECTS = PokemonPropertyExtractor { pokemon, properties -> properties.aspects = pokemon.aspects }
        @JvmField
        val LEVEL = PokemonPropertyExtractor { pokemon, properties -> properties.level = pokemon.level }
        @JvmField
        val GENDER = PokemonPropertyExtractor { pokemon, properties -> properties.gender = pokemon.gender }
        @JvmField
        val FRIENDSHIP = PokemonPropertyExtractor { pokemon, properties -> properties.friendship = pokemon.friendship }
        @JvmField
        val POKEBALL = PokemonPropertyExtractor { pokemon, properties ->  properties.pokeball = pokemon.caughtBall.name.toString() }
        @JvmField
        val NATURE = PokemonPropertyExtractor { pokemon, properties ->  properties.nature = pokemon.nature.name.toString() }
        @JvmField
        val ABILITY = PokemonPropertyExtractor { pokemon, properties ->  properties.ability = pokemon.ability.name }

        @JvmField
        val ALL = arrayOf(SPECIES, FORM, SHINY, ASPECTS, LEVEL, GENDER, FRIENDSHIP, POKEBALL, NATURE, ABILITY)
    }

    operator fun invoke(pokemon: Pokemon, properties: PokemonProperties)
}
