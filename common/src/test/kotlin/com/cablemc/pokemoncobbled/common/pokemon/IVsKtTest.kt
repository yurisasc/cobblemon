package com.cablemc.pokemoncobbled.common.pokemon

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class IVsKtTest {
    @Test
    fun `should create a randomized set of IVs with 3 perfect values`() {
        val ivs = IVs.createRandomIVs(3)
        var foundPerfects = 0
        for ((_, value) in ivs) {
            if (value == IVs.MAX_VALUE) {
                foundPerfects++
            }
        }
        assertTrue(foundPerfects >= 3)
    }
}