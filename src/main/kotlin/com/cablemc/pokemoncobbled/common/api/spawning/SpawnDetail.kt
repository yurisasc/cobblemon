package com.cablemc.pokemoncobbled.common.api.spawning

import com.cablemc.pokemoncobbled.common.api.spawning.condition.CompositeSpawningCondition
import com.cablemc.pokemoncobbled.common.api.spawning.condition.SpawningCondition
import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.spawner.Spawner

abstract class SpawnDetail {
    lateinit var type: String

    var id = ""
    var conditions = mutableListOf<SpawningCondition<*>>()
    var anticonditions = mutableListOf<SpawningCondition<*>>()
    var compositeCondition: CompositeSpawningCondition? = null

    var rarity = 0F
    var percentage = 0F

    var labels = mutableListOf<String>()
    var size = 1F

    // TODO consider precalculating the possible contexts

    open fun autoLabel() {}

    open fun isSatisfiedBy(ctx: SpawningContext): Boolean {
        if (!ctx.preFilter(this)) {
            return false
        } else if (conditions.isNotEmpty() && conditions.none { it.isSatisfiedBy(ctx) }) {
            return false
        } else if (anticonditions.isNotEmpty() && anticonditions.any { it.isSatisfiedBy(ctx) }) {
            return false
        } else if (compositeCondition?.satisfiedBy(ctx) == false) {
            return false
        }
        return true
    }

    abstract fun doSpawn(spawner: Spawner, ctx: SpawningContext): SpawningAction<*>
}