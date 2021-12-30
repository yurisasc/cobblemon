package com.cablemc.pokemoncobbled.common.pokemon

import com.cablemc.pokemoncobbled.common.pokemon.stats.Stat

open class FormData : SpeciesData {
    private var _baseStats: MutableMap<Stat, Int>? = null
    private var _maleRatio: Float? = null

    override var baseStats: MutableMap<Stat, Int> = _baseStats ?: species.baseStats
    override var maleRatio: Float = _maleRatio ?: species.maleRatio
    lateinit var species: Species
}