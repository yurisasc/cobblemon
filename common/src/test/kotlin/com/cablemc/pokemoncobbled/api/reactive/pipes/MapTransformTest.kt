/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.api.reactive.pipes

import com.cablemc.pokemoncobbled.common.api.reactive.SimpleObservable
import com.cablemc.pokemoncobbled.common.api.reactive.pipes.MapTransform
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