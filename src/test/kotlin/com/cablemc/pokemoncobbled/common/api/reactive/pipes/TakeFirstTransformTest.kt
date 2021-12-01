package com.cablemc.pokemoncobbled.common.api.reactive.pipes

import com.cablemc.pokemoncobbled.common.api.reactive.SimpleObservable
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class TakeFirstTransformTest {

    @Test
    fun `transformed observable will only take as many values as defined in constructor`() {
        val observable = SimpleObservable<Int>()
        val transformedObservable = observable.pipe(TakeFirstTransform(3))
        val results = mutableListOf<Int>()
        transformedObservable.subscribe { value -> results.add(value) }
        for (i in 1..5) { observable.emit(i) }
        assertEquals(3, results.size)
        assertEquals(6, results.reduce { a, b -> a + b })
    }

}