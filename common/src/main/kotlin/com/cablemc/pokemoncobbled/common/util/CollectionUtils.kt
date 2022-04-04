package com.cablemc.pokemoncobbled.common.util

import com.cablemc.pokemoncobbled.common.pokemon.PokemonStats
import com.cablemc.pokemoncobbled.common.api.pokemon.stats.Stat
import kotlin.math.max
import kotlin.random.Random

fun pokemonStatsOf(vararg entries: Pair<Stat, Int>): PokemonStats {
    val stats = PokemonStats()
    entries.forEach { (stat, amount) -> stats[stat] = amount }
    return stats
}

fun <T> Iterable<T>.weightedSelection(weightFunction: (T) -> Number): T? {
    var weightSum = 0F
    forEach { weightSum += max(0F, weightFunction(it).toFloat()) }
    val chosenSum = Random.Default.nextFloat() * weightSum
    weightSum = 0F
    forEach {
        val weight = weightFunction(it).toFloat()
        if (weight > 0) {
            weightSum += weight
            if (weightSum >= chosenSum) {
                return it
            }
        }
    }

    return null
}