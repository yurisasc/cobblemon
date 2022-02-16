package com.cablemc.pokemoncobbled.mod.config.value

annotation class IntValue(
    val defaultValue : Int,
    val min : Int = Int.MIN_VALUE,
    val max: Int = Int.MAX_VALUE
)