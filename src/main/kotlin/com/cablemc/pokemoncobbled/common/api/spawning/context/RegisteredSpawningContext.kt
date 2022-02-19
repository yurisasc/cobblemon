package com.cablemc.pokemoncobbled.common.api.spawning.context

import com.cablemc.pokemoncobbled.common.api.spawning.context.calculators.SpawningContextCalculator
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail

/**
 * A [SpawningContext] type that has been registered. Don't instantiate this
 * yourself because you're probably doing something wrong. Look at [SpawningContext.register].
 *
 * A default condition type name is provided for when a [SpawnDetail] is being interpreted
 * with a specific context - conditions in that detail can specify a specific condition
 * class, or if unspecified it will use the one here for convenience.
 *
 * Calculators for a custom context should be registered using [SpawningContextCalculator.register].
 *
 * @author Hiroku
 * @since January 31st, 2022
 */
data class RegisteredSpawningContext<T : SpawningContext>(
    val name: String,
    val clazz: Class<T>,
    val defaultCondition: String
)