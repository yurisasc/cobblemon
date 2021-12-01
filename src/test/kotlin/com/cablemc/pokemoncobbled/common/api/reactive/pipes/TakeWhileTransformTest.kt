package com.cablemc.pokemoncobbled.common.api.reactive.pipes

import com.cablemc.pokemoncobbled.common.api.reactive.SimpleObservable
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class TakeWhileTransformTest {

    @Test
    fun `transformed observable will only emit values so long as predicate is true`() {
        val observable = SimpleObservable<Int>()
        val transformedObservable = observable.pipe(TakeWhileTransform { value -> value < 6 })
        val results = mutableListOf<Int>()
        transformedObservable.subscribe { value -> results.add(value) }
        for (i in 1..10) { observable.emit(i) }
        assertEquals(5, results.size)
        assertEquals(15, results.reduce { a, b -> a + b })
    }

}