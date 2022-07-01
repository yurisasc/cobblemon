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