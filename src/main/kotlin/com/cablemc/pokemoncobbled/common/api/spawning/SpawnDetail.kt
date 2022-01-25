package com.cablemc.pokemoncobbled.common.api.spawning

import com.cablemc.pokemoncobbled.common.api.spawning.condition.SpawningCondition

abstract class SpawnDetail {
    lateinit var type: String

    var id = ""
    var conditions = mutableListOf<SpawningCondition<*>>()
    var anticonditions = mutableListOf<SpawningCondition<*>>()

    var rarity = 0F
    var percentage = 0F

    // Maybe properties in here could be sub-objects for detailing in what way it spawns in different contexts

    // Consider whether we want a spawn action still
    abstract fun doSpawn()
}