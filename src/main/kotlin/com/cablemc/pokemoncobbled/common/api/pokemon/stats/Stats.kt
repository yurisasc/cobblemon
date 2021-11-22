package com.cablemc.pokemoncobbled.common.api.pokemon.stats

import com.cablemc.pokemoncobbled.common.pokemon.stats.Stat

object Stats {
    private val allStats = mutableListOf<Stat>()
    private var statsArray: Array<Stat> = emptyArray()


    fun registerStat(stat: Stat): Stat {
        allStats.add(stat)
        // TODO probably mark the one that's been added as custom
        statsArray = allStats.toTypedArray()
        return stat
    }
}