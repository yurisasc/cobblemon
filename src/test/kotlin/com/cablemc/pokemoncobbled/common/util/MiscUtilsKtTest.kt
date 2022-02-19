package com.cablemc.pokemoncobbled.common.util

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

internal class MiscUtilsKtTest {
    @Test
    fun `should return true for same digit count version that is higher`() {
        val a = "2.1.2"
        val b = "2.1.1"
        assertTrue(a.isHigherVersion(b))
    }

    @Test
    fun `should return false for same digit count version that is lower`() {
        val a = "2.1.1"
        val b = "2.1.3"
        assertFalse(a.isHigherVersion(b))
    }

    @Test
    fun `should return true for smaller digit count but is higher`() {
        val a = "2.1"
        val b = "2.0.3"
        assertTrue(a.isHigherVersion(b))
    }

    @Test
    fun `should return false for smaller digit count`() {
        val a = "2.1"
        val b = "2.1.1"
        assertFalse(a.isHigherVersion(b))
    }

    @Test
    fun `should not explode when digits are not digits`() {
        val a = "a"
        val b = "1.2"
        assertDoesNotThrow { a.isHigherVersion(b) }
    }
}