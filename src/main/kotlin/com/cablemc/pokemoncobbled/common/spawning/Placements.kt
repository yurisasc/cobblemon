package com.cablemc.pokemoncobbled.common.spawning

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.pokemon.Species
import com.cablemc.pokemoncobbled.common.spawning.settings.MockSettings

/**
 * Where the Pokémon shall be placed
 *
 * TODO Requires adjustment when the SpeciesSpawningInfo is properly integrated
 */
enum class Placements {
    ON_GROUND {
        override fun getPossibleSpawns(): List<Species> {
            //return PokemonSpecies.species.filter { it.spawningInfo.placement == ON_GROUND }
            return PokemonSpecies.species.filter { MockSettings.spawningInfo.placement == ON_GROUND }
        }
    };

    abstract fun getPossibleSpawns(): List<Species>
}