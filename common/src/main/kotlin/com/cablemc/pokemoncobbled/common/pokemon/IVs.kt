package com.cablemc.pokemoncobbled.common.pokemon

import com.cablemc.pokemoncobbled.common.api.pokemon.stats.Stats
import com.cablemc.pokemoncobbled.common.util.randomNoCopy
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