/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.pokemon

import com.cablemc.pokemod.common.api.pokemon.stats.Stats

class EVs : PokemonStats() {
    override val acceptableRange = 0..MAX_STAT_VALUE
    // TODO: Force caps on total value

    companion object {
        const val MAX_STAT_VALUE = 252
        const val MAX_TOTAL_VALUE = 510

        fun createEmpty() : EVs {
            val evs = EVs()
            // Initialize base empty values
            for (stat in Stats.mainStats) {
                evs[stat] = 0
            }
            return evs
        }
    }
}