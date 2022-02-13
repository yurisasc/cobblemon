package com.cablemc.pokemoncobbled.common.util

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.resources.ResourceLocation
import kotlin.math.min

fun cobbledResource(path: String) = ResourceLocation(PokemonCobbled.MODID, path)

fun String.asTranslated(vararg data: Any) = TranslatableComponent(this, *data)
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