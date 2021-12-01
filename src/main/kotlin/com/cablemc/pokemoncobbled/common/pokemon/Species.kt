package com.cablemc.pokemoncobbled.common.pokemon

import com.cablemc.pokemoncobbled.common.pokemon.stats.Stat

class Species {
    var name: String = "bulbasaur"
    var nationalPokedexNumber = 1
    var baseStats = mutableMapOf<Stat, Int>()
    /** The ratio of the species being male. If -1, the Pokémon is genderless. */
    var maleRatio = 0.5
    var forms = mutableListOf(NORMAL_FORM)
}