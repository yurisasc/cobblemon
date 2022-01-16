package com.cablemc.pokemoncobbled.common.pokemon

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class IVsKtTest {
    @Test
    fun `should create a randomized set of IVs`() {
        val ivs = IVs.createRandomIVs()
        assertTrue(ivs != null)
    }
    @Test
    fun `should create a randomized set of IVs with 3 perfect values`() {
        val ivs = IVs.createRandomIVs(3)
        var foundPerfects = 0
        for(value in ivs.values) {
            if(value == IVs.maxStatValue) {
                foundPerfects++
            }
        }
        assertTrue(foundPerfects >= 3)
    }
}