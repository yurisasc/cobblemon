package com.cablemc.pokemoncobbled.common.api.pokemon

import com.cablemc.pokemoncobbled.common.pokemon.Species
import com.cablemc.pokemoncobbled.common.pokemon.SpeciesLoader

object PokemonSpecies {
    val BULBASAUR = register(SpeciesLoader.loadFromAssets("bulbasaur"))
    val EEVEE = register(SpeciesLoader.loadFromAssets("eevee"))

    private val allSpecies = mutableListOf(
        BULBASAUR,
        EEVEE
    )
    val species: List<Species>
        get() = species.toList()

    fun register(species: Species): Species {
        allSpecies.add(species)
        return species
    }

    fun getByName(name: String): Species? {
        return allSpecies.firstOrNull { species -> species.name == name }
    }

    fun count() = allSpecies.size
}