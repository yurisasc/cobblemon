/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.util.math

import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

infix fun Int.pow(power: Int): Int {
    return toDouble().pow(power.toDouble()).toInt()
}

fun Double?.orMax() = this ?: Double.MAX_VALUE
fun Double?.orMin() = this ?: Int.MIN_VALUE.toDouble()
fun Float?.orMax() = this ?: Int.MAX_VALUE.toFloat()
fun Float?.orMin() = this ?: Int.MIN_VALUE.toFloat()
fun Int?.orMax() = this ?: Int.MAX_VALUE
fun Int?.orMin() = this ?: Int.MIN_VALUE

fun Int.toRGB(): Triple<Double, Double, Double> {
    val r = ((this shr 16) and 0b11111111) / 255.0
    val g = ((this shr 8) and 0b11111111) / 255.0
    val b = (this and 0b11111111) / 255.0
    return Triple(r, g, b)
}

fun IntRange.intersects(other: IntRange) = start in other || endInclusive in other || other.start in this
fun IntRange.intersection(other: IntRange): IntRange {
    val intersectionStart = max(other.start, start)
    val intersectionEnd = min(other.endInclusive, endInclusive)

    return intersectionStart..intersectionEnd
}

fun Pair<Float, Float>.random() = Random.nextFloat() * (second - first) + first