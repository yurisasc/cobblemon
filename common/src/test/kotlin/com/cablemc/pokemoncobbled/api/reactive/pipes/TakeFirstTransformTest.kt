/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.api.reactive.pipes

import com.cablemc.pokemoncobbled.common.api.reactive.SimpleObservable
import com.cablemc.pokemoncobbled.common.api.reactive.pipes.TakeFirstTransform
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