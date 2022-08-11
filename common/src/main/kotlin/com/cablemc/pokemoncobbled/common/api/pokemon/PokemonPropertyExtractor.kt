package com.cablemc.pokemoncobbled.common.api.pokemon

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

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
    }

    operator fun invoke(pokemon: Pokemon, properties: PokemonProperties)
}
