/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.util.adapters

import io.mockk.mockk
import com.google.gson.JsonPrimitive
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class IntRangeAdapterTest {
    @Test
    fun `should parse positive range`() {
        val inputString = "1-10"
        val input = JsonPrimitive(inputString)
        val intRange = IntRangeAdapter.deserialize(input, mockk(), mockk())
        assertEquals(1, intRange.first)
        assertEquals(10, intRange.last)
    }

    @Test
    fun `should parse negative range`() {
        val inputString = "-10--2"
        val input = JsonPrimitive(inputString)
        val intRange = IntRangeAdapter.deserialize(input, mockk(), mockk())
        assertEquals(-10, intRange.first)
        assertEquals(-2, intRange.last)
    }

    @Test
    fun `should parse mixed range`() {
        val inputString = "-10-10"
        val input = JsonPrimitive(inputString)
        val intRange = IntRangeAdapter.deserialize(input, mockk(), mockk())
        assertEquals(-10, intRange.first)
        assertEquals(10, intRange.last)
    }

    @Test
    fun `should parse positive single number`() {
        val inputString = "10"
        val input = JsonPrimitive(inputString)
        val intRange = IntRangeAdapter.deserialize(input, mockk(), mockk())
        assertEquals(10, intRange.first)
        assertEquals(10, intRange.last)
    }

    @Test
    fun `should parse negative single number`() {
        val inputString = "-10"
        val input = JsonPrimitive(inputString)
        val intRange = IntRangeAdapter.deserialize(input, mockk(), mockk())
        assertEquals(-10, intRange.first)
        assertEquals(-10, intRange.last)
    }
}