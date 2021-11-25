package com.cablemc.pokemoncobbled.common.api.pokemon

import com.cablemc.pokemoncobbled.common.pokemon.Species
import com.cablemc.pokemoncobbled.common.pokemon.SpeciesLoader

object PokemonSpecies {
    private val allSpecies = mutableListOf<Species>()

    // TODO rework to create read-optimized views for dex number, name, others

    val BULBASAUR = register(SpeciesLoader.loadFromAssets("bulbasaur"))
    val EEVEE = register(SpeciesLoader.loadFromAssets("eevee"))

    val species: List<Species>
        get() = species.toList()

    fun register(species: Species): Species {
        allSpecies.add(species)
        return species
    }

    fun getByName(name: String): Species? {
        return allSpecies.firstOrNull { species -> species.name == name }
    }

    fun getByPokedexNumber(ndex: Int): Species? = allSpecies.find { it.nationalPokedexNumber == ndex }

    fun count() = allSpecies.size
}