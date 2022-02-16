package com.cablemc.pokemoncobbled.common.util

import com.cablemc.pokemoncobbled.common.util.math.pow
import kotlin.experimental.and
import kotlin.experimental.or

fun setBitForByte(byte: Byte, bit: Int, on: Boolean): Byte {
    val bitAsByte = 2 pow (bit - 1)
    return if (on) {
        byte or bitAsByte.toByte()
    } else {
        byte and (-bitAsByte - 1).toByte()
    }
}

fun getBitForByte(byte: Byte, bit: Int): Boolean {
    val bitAsByte = 2 pow (bit - 1)
    return (byte and bitAsByte.toByte()) != 0.toByte()
}