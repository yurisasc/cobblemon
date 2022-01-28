package com.cablemc.pokemoncobbled.common.pokemon

import com.cablemc.pokemoncobbled.common.api.pokemon.stats.Stats
import kotlin.random.Random

class EVs : PokemonStats() {

    // TODO: Force caps on values

    companion object {
        val maxStatValue = 252
        val maxTotalValue = 510

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