package com.cablemc.pokemoncobbled.common.util.math

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class SimpleMathExtensionsKtTest {
    @Test
    fun `intersects function should work`() {
        val digits = 0..10
        val oneToOneHundred = 1..100
        val negativeOneHundredToZero = -100..0
        val allNumbers = Int.MIN_VALUE..Int.MAX_VALUE
        assertTrue(digits.intersects(oneToOneHundred))
        assertTrue(digits.intersects(negativeOneHundredToZero))
        assertFalse(oneToOneHundred.intersects(negativeOneHundredToZero))
        assertTrue(digits.intersects(allNumbers))
    }

    @Test
    fun `intersection function should work for first being lower`() {
        val first = 1..10
        val second = 5..20
        val intersection = first.intersection(second)
        assertEquals(5, intersection.first)
        assertEquals(10, intersection.last)
    }

    @Test
    fun `intersection function should work for first being higher`() {
        val first = 12..20
        val second = 3..14
        val intersection = first.intersection(second)
        assertEquals(12, intersection.first)
        assertEquals(14, intersection.last)
    }

    @Test
    fun `intersection function should work for first being inside other`() {
        val first = 12..20
        val second = 1..50
        val intersection = first.intersection(second)
        assertEquals(12, intersection.first)
        assertEquals(20, intersection.last)
    }
}