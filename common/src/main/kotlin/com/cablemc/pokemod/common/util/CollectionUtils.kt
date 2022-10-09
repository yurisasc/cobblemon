/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.util

import com.cablemc.pokemod.common.api.pokemon.stats.Stat
import com.cablemc.pokemod.common.pokemon.EVs
import com.cablemc.pokemod.common.pokemon.IVs
import com.cablemc.pokemod.common.pokemon.PokemonStats
import kotlin.math.max
import kotlin.random.Random

fun ivsOf(vararg entries: Pair<Stat, Int>): PokemonStats {
    val stats = IVs()
    entries.forEach { (stat, amount) -> stats[stat] = amount }
    return stats
}

fun evsOf(vararg entries: Pair<Stat, Int>): PokemonStats {
    val stats = EVs()
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

fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    val t1 = this[index1]
    val t2 = this[index2]
    this[index1] = t2
    this[index2] = t1
}