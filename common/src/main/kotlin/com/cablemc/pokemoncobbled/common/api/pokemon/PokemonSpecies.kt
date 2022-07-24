package com.cablemc.pokemoncobbled.common.api.pokemon

import com.cablemc.pokemoncobbled.common.pokemon.Species
import com.cablemc.pokemoncobbled.common.pokemon.SpeciesLoader

object PokemonSpecies {

    private val speciesNames = hashMapOf<String, Species>()
    private val speciesDex = hashMapOf<Int, Species>()

    // TODO rework to create read-optimized views for dex number, name, others
    // Just a quick workaround out of necessity for case-insensitive lookup, when rework is done please keep that functionality

    val BULBASAUR = register(SpeciesLoader.loadFromAssets("bulbasaur"))
    val IVYSAUR = register(SpeciesLoader.loadFromAssets("ivysaur"))
    val VENUSAUR = register(SpeciesLoader.loadFromAssets("venusaur"))
    val CHARMANDER = register(SpeciesLoader.loadFromAssets("charmander"))
    val CHARMELEON = register(SpeciesLoader.loadFromAssets("charmeleon"))
    val CHARIZARD = register(SpeciesLoader.loadFromAssets("charizard"))
    val SQUIRTLE = register(SpeciesLoader.loadFromAssets("squirtle"))
    val WARTORTLE = register(SpeciesLoader.loadFromAssets("wartortle"))
    val BLASTOISE = register(SpeciesLoader.loadFromAssets("blastoise"))
    val CATERPIE = register(SpeciesLoader.loadFromAssets("caterpie"))
    val METAPOD = register(SpeciesLoader.loadFromAssets("metapod"))
    val BUTTERFREE = register(SpeciesLoader.loadFromAssets("butterfree"))
    val WEEDLE = register(SpeciesLoader.loadFromAssets("weedle"))
    val KAKUNA = register(SpeciesLoader.loadFromAssets("kakuna"))
    val BEEDRILL = register(SpeciesLoader.loadFromAssets("beedrill"))
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
    val RATICATE = register(SpeciesLoader.loadFromAssets("raticate"))



    val species: List<Species>
        get() = this.speciesNames.values.toList()

    fun register(species: Species): Species {
        this.speciesNames[species.name.lowercase()] = species
        this.speciesDex[species.nationalPokedexNumber] = species
        species.forms.forEach { it.initialize(species) }
        return species
    }

    fun getByName(name: String) = this.speciesNames[name.lowercase()]

    fun getByPokedexNumber(ndex: Int) = this.speciesDex[ndex]

    fun count() = this.speciesNames.size

}