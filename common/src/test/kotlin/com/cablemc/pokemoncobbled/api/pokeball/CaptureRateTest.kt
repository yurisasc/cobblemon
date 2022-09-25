/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.api.pokeball

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.math.max
import kotlin.math.min

internal class CaptureRateTest {

    @Test
    fun `test difficulty`() {
        var host = 10.0f
        var target = 100.0f

        assertEquals(0.1f, this.consolidate(host, target))

        host = 50.0f
        assertEquals(0.5f, this.consolidate(host, target))

        host = 100.0f
        assertEquals(1.0f, this.consolidate(host, target))

        target = 50.0f
        assertEquals(1.0f, this.consolidate(host, target))
    }

    private fun consolidate(host: Float, target: Float) : Float {
        return min(1.0f, max(0.1f, host / target))
    }

}
