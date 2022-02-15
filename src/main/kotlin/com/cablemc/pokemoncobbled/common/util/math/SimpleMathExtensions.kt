package com.cablemc.pokemoncobbled.common.util.math

import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

infix fun Int.pow(power: Int): Int {
    return toDouble().pow(power.toDouble()).toInt()
}

fun Double?.orMax() = this ?: Double.MAX_VALUE
fun Double?.orMin() = this ?: Int.MIN_VALUE.toDouble()
fun Float?.orMax() = this ?: Int.MAX_VALUE.toFloat()
fun Float?.orMin() = this ?: Int.MIN_VALUE.toFloat()
fun Int?.orMax() = this ?: Int.MAX_VALUE
fun Int?.orMin() = this ?: Int.MIN_VALUE

fun IntRange.intersects(other: IntRange) = start in other || endInclusive in other
fun IntRange.intersection(other: IntRange): IntRange {
    val intersectionStart = max(other.start, start)
    val intersectionEnd = min(other.endInclusive, endInclusive)

    return intersectionStart..intersectionEnd
}