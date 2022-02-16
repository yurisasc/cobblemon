package com.cablemc.pokemoncobbled.common.utils.collections

class ImmutableArray<T>(private vararg val values: T) {

    operator fun get(index: Int) = values[index]

}

inline fun <reified T> immutableArrayOf(vararg values: T) = ImmutableArray(*values)