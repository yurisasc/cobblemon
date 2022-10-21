/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.pokemon

import com.cablemc.pokemod.common.api.item.Flavor
import com.cablemc.pokemod.common.api.pokemon.stats.Stat
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