package com.cablemc.pokemoncobbled.common.api.pokemon

import com.cablemc.pokemoncobbled.common.pokemon.Species
import com.cablemc.pokemoncobbled.common.pokemon.SpeciesLoader

object PokemonSpecies {
    private val allSpecies = mutableListOf<Species>()

    // TODO rework to create read-optimized views for dex number, name, others

    val BULBASAUR = register(SpeciesLoader.loadFromAssets("bulbasaur"))
    val IVYSAUR = register(SpeciesLoader.loadFromAssets("ivysaur"))
    val VENUSAUR = register(SpeciesLoader.loadFromAssets("venusaur"))
    val CHARMANDER = register(SpeciesLoader.loadFromAssets("charmander"))
    val CHARMELEON = register(SpeciesLoader.loadFromAssets("charmeleon"))
    val CHARIZARD = register(SpeciesLoader.loadFromAssets("charizard"))
    val SQUIRTLE = register(SpeciesLoader.loadFromAssets("squirtle"))
    val WARTORTLE = register(SpeciesLoader.loadFromAssets("wartortle"))
    val BLASTOISE = register(SpeciesLoader.loadFromAssets("blastoise"))
    val BUTTERFREE = register(SpeciesLoader.loadFromAssets("butterfree"))
    val PIDGEY = register(SpeciesLoader.loadFromAssets("pidgey"))
    val PIDGEOTTO = register(SpeciesLoader.loadFromAssets("pidgeotto"))
    val PIDGEOT = register(SpeciesLoader.loadFromAssets("pidgeot"))
    val EKANS = register(SpeciesLoader.loadFromAssets("ekans"))
    val ZUBAT = register(SpeciesLoader.loadFromAssets("zubat"))
    val DIGLETT = register(SpeciesLoader.loadFromAssets("diglett"))
    val DUGTRIO = register(SpeciesLoader.loadFromAssets("dugtrio"))
    val MAGIKARP = register(SpeciesLoader.loadFromAssets("magikarp"))
    val GYARADOS = register(SpeciesLoader.loadFromAssets("gyarados"))
    val EEVEE = register(SpeciesLoader.loadFromAssets("eevee"))
    val RATTATA = register(SpeciesLoader.loadFromAssets("rattata"))

    val species: List<Species>
        get() = allSpecies

    fun register(species: Species): Species {
        allSpecies.add(species)
        species.forms.forEach { it.species = species }
        return species
    }

    fun getByName(name: String): Species? {
        return allSpecies.firstOrNull { species -> species.name == name }
    }

    fun getByPokedexNumber(ndex: Int): Species? = allSpecies.find { it.nationalPokedexNumber == ndex }

    fun count() = allSpecies.size
}