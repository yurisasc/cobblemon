package com.cablemc.pokemoncobbled.common.api.spawning.condition

import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext

/**
 *
 * @since January 26th, 2022
 */
class CompositeSpawningCondition {
    var conditions = mutableListOf<SpawningCondition<*>>()
    var anticonditions = mutableListOf<SpawningCondition<*>>()

    fun satisfiedBy(ctx: SpawningContext): Boolean {
        return if (conditions.isNotEmpty() && conditions.none { it.isSatisfiedBy(ctx) }) {
            false
        } else {
            !(anticonditions.isNotEmpty() && anticonditions.any { it.isSatisfiedBy(ctx) })
        }
    }
}