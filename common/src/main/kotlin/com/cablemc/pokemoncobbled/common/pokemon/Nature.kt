package com.cablemc.pokemoncobbled.common.pokemon

import com.cablemc.pokemoncobbled.common.api.item.Flavor
import com.cablemc.pokemoncobbled.common.api.pokemon.stats.Stat
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper.floor

class Nature(
    val name: Identifier,
    val increasedStat: Stat?,
    val decreasedStat: Stat?,
    val favoriteFlavor: Flavor?,
    val dislikedFlavor: Flavor?
) {
    fun modifyStat(stat: Stat, value: Int): Int {
        return when (stat) {
            increasedStat -> floor(value * 1.1)
            decreasedStat -> floor(value * 0.9)
            else -> value
        }
    }
}