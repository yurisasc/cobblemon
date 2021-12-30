package com.cablemc.pokemoncobbled.common.pokemon

import com.cablemc.pokemoncobbled.common.pokemon.stats.Stat

interface SpeciesData {
    var baseStats: MutableMap<Stat, Int>
    var maleRatio: Float
    /** The ratio of the species being male. If -1, the Pokémon is genderless. */

    // @SerializedName
}