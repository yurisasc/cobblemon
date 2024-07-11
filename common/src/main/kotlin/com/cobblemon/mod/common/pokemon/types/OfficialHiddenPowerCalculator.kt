/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.types

import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.api.types.HiddenPowerCalculator
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.registry.CobblemonRegistries
import net.minecraft.resources.ResourceKey

object OfficialHiddenPowerCalculator : HiddenPowerCalculator {

    private val hiddenPowerTable: Array<ResourceKey<ElementalType>> = arrayOf(
        ElementalTypes.FIGHTING,
        ElementalTypes.FLYING,
        ElementalTypes.POISON,
        ElementalTypes.GROUND,
        ElementalTypes.ROCK,
        ElementalTypes.BUG,
        ElementalTypes.GHOST,
        ElementalTypes.STEEL,
        ElementalTypes.FIRE,
        ElementalTypes.WATER,
        ElementalTypes.GRASS,
        ElementalTypes.ELECTRIC,
        ElementalTypes.PSYCHIC,
        ElementalTypes.ICE,
        ElementalTypes.DRAGON,
        ElementalTypes.DARK
    )
    override fun calculate(pokemon: Pokemon): ElementalType {
        val ivs = Stats.PERMANENT.map { pokemon.ivs[it]
            ?: throw IllegalStateException("Using ${this::class.simpleName} but the official stats are not present") }
        var tableIndex = 0
        ivs.forEachIndexed { index, it ->
            tableIndex += (it % 2) shl index
        }
        tableIndex = tableIndex * 15 / 63
        val key = this.hiddenPowerTable[tableIndex.coerceAtMost(this.hiddenPowerTable.size - 1)]
        return CobblemonRegistries.ELEMENTAL_TYPE.get(key)!!
    }
}