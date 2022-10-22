/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.util

import com.cablemc.pokemod.common.Pokemod
import kotlin.math.min
import net.minecraft.text.Text
import net.minecraft.util.Identifier

fun pokemodResource(path: String) = Identifier(Pokemod.MODID, path)

fun String.asTranslated() = Text.translatable(this)
fun String.asResource() = Identifier(this)
fun String.asTranslated(vararg data: Any) = Text.translatable(this, *data)
fun String.isInt() = this.toIntOrNull() != null
fun String.isHigherVersion(other: String): Boolean {
    val thisSplits = split(".")
    val thatSplits = other.split(".")

    val thisCount = thisSplits.size
    val thatCount = thatSplits.size

    val min = min(thisCount, thatCount)
    for (i in 0 until min) {
        val thisDigit = thisSplits[i].toIntOrNull()
        val thatDigit = thatSplits[i].toIntOrNull()
        if (thisDigit == null || thatDigit == null) {
            return false
        }

        if (thisDigit > thatDigit) {
            return true
        } else if (thisDigit < thatDigit) {
            return false
        }
    }

    return thisCount > thatCount
}

fun String.substitute(placeholder: String, value: Any?) = replace("{{$placeholder}}", value?.toString() ?: "")