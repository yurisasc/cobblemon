package com.cablemc.pokemoncobbled.common.spawning

import com.cablemc.pokemoncobbled.common.spawning.conditions.time.ITimeCondition

/**
 * Data to be put in the species json
 * this controls the Pokémon's spawn conditions
 */
data class SpeciesSpawningInfo(
    var spawnTimes: MutableList<ITimeCondition>,
    var biomes: MutableList<String>,
    var placement: Placements
)
