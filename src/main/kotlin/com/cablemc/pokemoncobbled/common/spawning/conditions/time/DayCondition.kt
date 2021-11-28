package com.cablemc.pokemoncobbled.common.spawning.conditions.time

import com.cablemc.pokemoncobbled.common.spawning.SpawnInfo

/**
 * Currently always returns true, but I don't know if we want to implement it this way
 */
class DayCondition: ITimeCondition {
    override fun name(): String {
        return "DayCondition"
    }

    override fun canSpawn(spawnInfo: SpawnInfo): Boolean {
        return true
    }
}