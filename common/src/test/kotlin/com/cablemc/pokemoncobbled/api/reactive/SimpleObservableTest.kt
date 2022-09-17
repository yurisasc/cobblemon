/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.api.reactive

import com.cablemc.pokemoncobbled.common.api.reactive.SimpleObservable
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
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
        for (i in 1..3) { observable.emit(i) }
        assertEquals(3, callbackValues.size)
        assertEquals(6, callbackValues.reduce { a, b -> a + b })
    }

    @Test
    fun `unsubscribing from observable stops observable from invoking callback`() {
        val observable = SimpleObservable<Int>()
        val callbackValues = mutableListOf<Int>()
        val subscription = observable.subscribe { value -> callbackValues.add(value) }
        observable.emit(1)
        observable.emit(2)
        observable.emit(3)
        observable.unsubscribe(subscription)
        observable.emit(4)
        assertEquals(3, callbackValues.size)
        assertEquals(6, callbackValues.reduce { a, b -> a + b })
    }

    @Disabled("Might be redundant, depending on if implementation behaviour makes sense. When decision is made, this should be re-enabled or removed.")
    @Test
    fun `unsubscribing in another subscription obeys immediately and does not invoke on observable emit`() {
        val observable = SimpleObservable<Unit>()
        var result = false
        val subscription = observable.subscribe { result = true }
        observable.subscribe { subscription.unsubscribe() }
        observable.emit(Unit)
        assertFalse(result)
    }

}