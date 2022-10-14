/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.util

import com.cablemc.pokemod.common.util.math.pow
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