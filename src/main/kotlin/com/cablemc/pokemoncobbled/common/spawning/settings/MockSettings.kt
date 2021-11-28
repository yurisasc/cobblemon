package com.cablemc.pokemoncobbled.common.spawning.settings

import com.cablemc.pokemoncobbled.common.spawning.Placements
import com.cablemc.pokemoncobbled.common.spawning.SpeciesSpawningInfo
import com.cablemc.pokemoncobbled.common.spawning.conditions.time.DayCondition

/**
 * Temporary MockSettings
 */
object MockSettings {

    val maxDist = 10
    val maxHeight = 10

    var spawningInfo = SpeciesSpawningInfo(mutableListOf(DayCondition()), mutableListOf("minecraft:forest"), Placements.ON_GROUND)


}