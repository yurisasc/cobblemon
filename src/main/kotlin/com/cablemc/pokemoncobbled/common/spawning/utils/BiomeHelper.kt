package com.cablemc.pokemoncobbled.common.spawning.utils

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.pokemon.Species
import net.minecraft.world.level.biome.Biome

object BiomeHelper {

    /**
     * TODO: Compare with SpeciesSpawningInfo to properly return possible spawns in a Biome
     */
    fun possibleSpawns(biome: Biome): List<Species> {
        return PokemonSpecies.species
    }

}