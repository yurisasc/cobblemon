package com.cablemc.pokemoncobbled.common.util

import net.minecraft.util.Identifier

fun Identifier.endsWith(suffix: String): Boolean {
    return this.toString().endsWith(suffix)
}