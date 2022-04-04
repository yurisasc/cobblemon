package com.cablemc.pokemoncobbled.common.pokemon

import com.cablemc.pokemoncobbled.common.api.pokemon.stats.Stats

class EVs : PokemonStats() {

    // TODO: Force caps on values

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