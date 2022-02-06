package com.cablemc.pokemoncobbled.common.api.spawning.context

import com.cablemc.pokemoncobbled.common.api.spawning.context.calculators.SpawningContextCalculator

/**
 * A [SpawningContext] type that has been registered. Don't instantiate this
 * yourself because you're probably doing something wrong. Look at [SpawningContext.register].
 *
 * Calculators for a custom context should be registered using [SpawningContextCalculator.register].
 *
 * @author Hiroku
 * @since January 31st, 2022
 */
data class RegisteredSpawningContext<T : SpawningContext>(
    val name: String,
    val clazz: Class<T>
)