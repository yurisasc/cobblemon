/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.types.hiddenpower

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.Environment
import com.cobblemon.mod.common.api.pokemon.stats.Stat
import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.pokemon.Pokemon
import com.mojang.serialization.Codec

/**
 * Responsible for determining if a [Pokemon] can use the hidden power of a given type.
 * This will be attached to a [ElementalType.hiddenPowerRequirement].
 *
 * @property statMap The stat map that checks [IvCondition]s for each IV value of a [Pokemon].
 *
 * @throws IllegalArgumentException if not every stat of type [Stat.Type.PERMANENT] is present.
 */
class HiddenPowerRequirement(internal val statMap: Map<Stat, IvCondition>) {

    init {
        if (Cobblemon.implementation.environment() == Environment.SERVER && !this.statMap.keys.containsAll(Cobblemon.statProvider.ofType(Stat.Type.PERMANENT))) {
            throw IllegalArgumentException("Cannot create a ${this::class.simpleName} when missing permanent stats")
        }
    }

    /**
     * Checks if the given [Pokemon] passes this requirement.
     *
     * @param pokemon The [Pokemon] being checked.
     * @return If this requirement is passed against the [pokemon].
     */
    fun isValid(pokemon: Pokemon): Boolean = pokemon.ivs.all { (stat, value) -> this.statMap[stat]!!.fits(value) }

    companion object {

        val CODEC: Codec<HiddenPowerRequirement> = Codec.unboundedMap(Stat.CODEC, IvCondition.CODEC)
            .xmap(::HiddenPowerRequirement, HiddenPowerRequirement::statMap)

    }

}