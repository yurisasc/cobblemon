/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.util

fun <T> List<T>.random(amount: Int): List<T> {
    val values = mutableListOf<T>()
    for (i in 1..amount) {
        values.add(random())
    }
    return values
}

fun <T> List<T>.randomNoCopy(amount: Int): List<T> {
    val values = mutableListOf<T>()
    var amountLeft = amount
    while (amountLeft != 0) {
        val random = random()
        if (!values.contains(random)) {
            values.add(random)
            amountLeft--
        }
    }
    return values
}