package com.cablemc.pokemoncobbled.common.api.reactive.pipes

import com.cablemc.pokemoncobbled.common.api.reactive.SimpleObservable
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class MapTransformTest {

    @Test
    fun `observable with map transform transforms all values emitted`() {
        val observable = SimpleObservable<Int>()
        val transformedObservable = observable.pipe(MapTransform { value -> value / 2.0 })
        val results = mutableListOf<Double>()
        transformedObservable.subscribe { value -> results.add(value) }
        for (i in 1..3) { observable.emit(i) }
        assertEquals(3, results.size)
        assertEquals(3.0, results.reduce { a, b -> a + b })
    }

}