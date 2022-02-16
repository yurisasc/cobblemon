package com.cablemc.pokemoncobbled.common.utils.math

import kotlin.math.pow

infix fun Int.pow(power: Int): Int {
    return toDouble().pow(power.toDouble()).toInt()
}