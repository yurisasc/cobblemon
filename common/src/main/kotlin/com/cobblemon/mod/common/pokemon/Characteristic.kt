/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon

import com.cobblemon.mod.common.api.pokemon.Characteristics
import com.cobblemon.mod.common.api.pokemon.stats.Stat
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import net.minecraft.util.Identifier
import java.util.*

/**
 * Represents a Pokémon's characteristic, which is based on
 * their highest IV(s).
 */
class Characteristic(
    val name: Identifier,
    val displayName: String,
    val highStat: Stat,
    val ivs: Set<Int>,
) {
    companion object {
        fun calculateCharacteristic(pokemon: Pokemon): Characteristic {
            return calculateCharacteristic(pokemon.ivs, pokemon.uuid)
        }

        fun calculateCharacteristic(iVs: IVs, uuid: UUID): Characteristic {
            val stats = arrayOf<Stat>(
                Stats.HP, Stats.ATTACK, Stats.DEFENCE,
                Stats.SPEED, Stats.SPECIAL_ATTACK, Stats.SPECIAL_DEFENCE
            )
            val values = mutableListOf<Pair<Stat, Int>>()

            // Add IVs in default order
            stats.iterator().forEach { stat -> values.add(Pair(stat, iVs.getOrDefault(stat))) }

            // Rotate list so tiebreaker start index is first
            Collections.rotate(values, -(uuid.hashCode() % 6))

            // Get max IV considering tiebreaker
            val maxIV =
                values.maxWithOrNull { pair1, pair2 -> pair1.second.compareTo(pair2.second) } ?: Pair(Stats.HP, 0)

            return Characteristics.all()
                .find { characteristic -> characteristic.highStat == maxIV.first && characteristic.ivs.contains(maxIV.second) }
                ?: Characteristics.LOVES_TO_EAT
        }
    }
}