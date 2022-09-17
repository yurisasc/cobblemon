/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api.moves

import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class MoveSetTest {
    @Test
    fun `it should swap two moves properly`() {
        val moveset = MoveSet()
        val move1 = Move(11, 2, mockk())
        val move2 = Move(10, 4, mockk())
        moveset.setMove(0, move1)
        moveset.setMove(1, move2)
        moveset.swapMove(0, 1)
        assertEquals(move1, moveset[1])
        assertEquals(move2, moveset[0])
    }
}