/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.pokemon

import com.cablemc.pokemod.common.api.pokemon.stats.Stats
import com.cablemc.pokemod.common.util.randomNoCopy
import kotlin.random.Random

class IVs : PokemonStats() {
    override val acceptableRange = 0..MAX_VALUE
    // TODO: Hyper training

    companion object {
        const val MAX_VALUE = 31

        fun createRandomIVs(minPerfectIVs : Int = 0) : IVs {
            val ivs = IVs()

            // Initialize base random values
            for (stat in Stats.mainStats) {
                ivs[stat] = Random.nextInt(MAX_VALUE)
            }

            // Add in minimum perfect IVs
            if (minPerfectIVs > 0) {
                val perfectStats = Stats.mainStats.randomNoCopy(minPerfectIVs)
                for (stat in perfectStats) {
                    ivs[stat] = MAX_VALUE
                }
            }
            return ivs
        }
    }
}