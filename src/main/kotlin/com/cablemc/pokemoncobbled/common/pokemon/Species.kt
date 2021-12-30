package com.cablemc.pokemoncobbled.common.pokemon

import com.cablemc.pokemoncobbled.common.pokemon.stats.Stat

class Species : SpeciesData {
    var name: String = "bulbasaur"
    var nationalPokedexNumber = 1

    override var baseStats: MutableMap<Stat, Int> = mutableMapOf()
    /** The ratio of the species being male. If -1, the Pokémon is genderless. */
    override var maleRatio = 0.5F

    var forms = mutableListOf(NORMAL_FORM)
}