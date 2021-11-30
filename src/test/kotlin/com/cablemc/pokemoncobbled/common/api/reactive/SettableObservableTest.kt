package com.cablemc.pokemoncobbled.common.api.reactive

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class SettableObservableTest {

    @Test
    fun `setting observable to same value does not notify subscribers`() {
        val observable = SettableObservable(false)
        var callbackExecuted = false
        observable.subscribe { callbackExecuted = true }
        observable.set(false)
        assertFalse(callbackExecuted)
    }

    @Test
    fun `setting observable value to null when its currently null does not notify subscribers`() {
        val observable = SettableObservable<Boolean?>(null)
        var callbackExecuted = false
        observable.subscribe { callbackExecuted = true }
        observable.set(null)
        assertFalse(callbackExecuted)
    }

    @Test
    fun `get returns last value emitted from observable`() {
        val observable = SettableObservable(123)
        assertEquals(123, observable.get())
    }

    @Test
    fun `get returns nullable last value emitted from observable`() {
        val observable = SettableObservable<Int?>(null)
        assertEquals(null, observable.get())
    }

}