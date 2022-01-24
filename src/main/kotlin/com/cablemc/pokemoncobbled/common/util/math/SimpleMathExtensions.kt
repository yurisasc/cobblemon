package com.cablemc.pokemoncobbled.common.util.math

import kotlin.math.pow

infix fun Int.pow(power: Int): Int {
    return toDouble().pow(power.toDouble()).toInt()
}

fun Double?.orMax() = this ?: Double.MAX_VALUE
fun Double?.orMin() = this ?: Double.MIN_VALUE
fun Float?.orMax() = this ?: Float.MAX_VALUE
fun Float?.orMin() = this ?: Float.MIN_VALUE
fun Int?.orMax() = this ?: Int.MAX_VALUE
fun Int?.orMin() = this ?: Int.MIN_VALUE