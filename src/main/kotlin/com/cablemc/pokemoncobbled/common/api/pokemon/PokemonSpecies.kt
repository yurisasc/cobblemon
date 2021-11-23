package com.cablemc.pokemoncobbled.common.api.pokemon

import com.cablemc.pokemoncobbled.common.pokemon.Species
import com.cablemc.pokemoncobbled.common.pokemon.SpeciesLoader

object PokemonSpecies {
    private val allSpecies = mutableListOf<Species>()

    val BULBASAUR = register(SpeciesLoader.loadFromAssets("bulbasaur"))
    val EEVEE = register(SpeciesLoader.loadFromAssets("eevee"))

    fun register(species: Species): Species {
        allSpecies.add(species)
        return species
    }

    fun count() = allSpecies.size
}