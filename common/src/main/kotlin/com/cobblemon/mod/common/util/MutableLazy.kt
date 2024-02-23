package com.cobblemon.mod.common.util

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class MutableLazy<T>(private var initializer: (() -> T)?) : ReadWriteProperty<Any?, T> {
    private var value: T? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (value == null) {
            value = initializer?.invoke()
            initializer = null
        }
        return value!!
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }
}

fun <T> mutableLazy(initializer: () -> T) = MutableLazy(initializer)
