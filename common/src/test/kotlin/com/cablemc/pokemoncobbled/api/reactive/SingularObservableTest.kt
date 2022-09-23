/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.api.reactive

import com.cablemc.pokemoncobbled.common.api.reactive.SingularObservable
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class SingularObservableTest {

    @Test
    fun `emitting more than once causes exception`() {
        val observable = SingularObservable<Unit>()
        observable.emit(Unit)
        val exception = assertThrows(IllegalStateException::class.java) { observable.emit(Unit) }
        assertEquals("This observable is already completed!", exception.message)
    }

}