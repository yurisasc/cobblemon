package com.cablemc.pokemoncobbled.common.spawning.conditions

import com.cablemc.pokemoncobbled.common.spawning.SpawnInfo

/**
 * Superclass for conditions?
 */
interface ICondition {

    fun name(): String

    fun canSpawn(spawnInfo: SpawnInfo): Boolean

}