/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.api.reactive.pipes

import com.cablemc.pokemoncobbled.common.api.reactive.SimpleObservable
import com.cablemc.pokemoncobbled.common.api.reactive.pipes.FilterTransform
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class FilterTransformTest {

    @Test
    fun `transformation filters out all even numbers from a integer stream`() {
        val observable = SimpleObservable<Int>()
        val transformedObservable = observable.pipe(FilterTransform { it % 2 != 0})
        val results = mutableListOf<Int>()
        transformedObservable.subscribe { value -> results.add(value) }
        for (i in 1..10) { observable.emit(i) }
        assertEquals(5, results.size)
        results.forEach { value -> assertTrue(value % 2 != 0) }
    }

}