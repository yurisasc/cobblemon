package com.cablemc.pokemoncobbled.api.reactive.pipes

import com.cablemc.pokemoncobbled.common.api.reactive.SimpleObservable
import com.cablemc.pokemoncobbled.common.api.reactive.pipes.IgnoreFirstTransform
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class IgnoreFirstTransformTest {

    @Test
    fun `observable with transform ignores first three values`() {
        val observable = SimpleObservable<Int>()
        val transformedObservable = observable.pipe(IgnoreFirstTransform(3))
        val results = mutableListOf<Int>()
        transformedObservable.subscribe { value -> results.add(value) }
        for (i in 1..4) { observable.emit(i) }
        assertEquals(1, results.size)
        assertEquals(4, results.firstOrNull())
    }

}