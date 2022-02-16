package com.cablemc.pokemoncobbled.forge.mod.config.value

annotation class DoubleValue(
    val defaultValue : Double,
    val min : Double = Double.MIN_VALUE,
    val max: Double = Double.MAX_VALUE
)