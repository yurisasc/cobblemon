package com.cablemc.pokemoncobbled.common.api.spawning.context

import com.cablemc.pokemoncobbled.common.api.spawning.context.calculators.SpawningContextCalculator

data class RegisteredSpawningContext<T : SpawningContext>(
    val name: String,
    val clazz: Class<T>,
    val calculator: SpawningContextCalculator<*, T>
)