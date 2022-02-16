package com.cablemc.pokemoncobbled.mod.config.value

annotation class LongValue(
    val defaultValue : Long,
    val min : Long = Long.MIN_VALUE,
    val max: Long = Long.MAX_VALUE
)