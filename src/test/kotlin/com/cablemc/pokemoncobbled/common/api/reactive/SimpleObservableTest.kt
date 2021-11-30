package com.cablemc.pokemoncobbled.common.api.reactive

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class SimpleObservableTest {

    @Test
    fun `emitting a value from observable calls back to subscription`() {
        val observable = SimpleObservable<Unit>()
        var functionWasExecuted = false
        observable.subscribe { functionWasExecuted = true }
        observable.emit(Unit)
        assertEquals(true, functionWasExecuted)
    }

    @Test
    fun `emitting a series of values from observable calls back for each emitted value`() {
        val observable = SimpleObservable<Int>()
        val callbackValues = mutableListOf<Int>()
        observable.subscribe { value -> callbackValues.add(value) }
        observable.emit(1)
        observable.emit(2)
        observable.emit(3)
        assertEquals(3, callbackValues.size)
        assertEquals(6, callbackValues.reduce { a, b -> a + b })
    }

}