package com.cablemc.pokemoncobbled.common.api.spawning.condition

import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext

/**
 * A spawning condition that is composed of a list of conditions and anticonditions.
 *
 * A composite condition passes if both of the following are true:
 * - The conditions list is empty, or any of the conditions match.
 * - The anticonditions list is empty, or none of the anticonditions match.
 *
 * In other words, if the anticonditions list has five elements, then only one of those
 * needs to be true for the spawn to be canceled. Meanwhile, if there are five elements
 * in the conditions list, then all of them would need to be false for the spawn to be
 * canceled.
 *
 * @author Hiroku
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