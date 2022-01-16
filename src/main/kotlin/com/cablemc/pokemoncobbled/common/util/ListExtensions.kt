package com.cablemc.pokemoncobbled.common.util

fun <T> List<T>.random(amount: Int): List<T> {
    val values = mutableListOf<T>()
    for(i in 1..amount) {
        values.add(random())
    }
    return values
}