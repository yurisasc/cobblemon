package com.cablemc.pokemoncobbled.common.util.adapters

import com.cablemc.pokemoncobbled.common.api.spawning.condition.TimeRange
import com.google.gson.JsonPrimitive
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class TimeRangeAdapterTest {
    @Test
    fun `should parse recognized ranges as well as range encoded text`() {
        val inputString = "12000-13000,night"
        val input = JsonPrimitive(inputString)
        val timeRange = TimeRangeAdapter.deserialize(input, mockk(), mockk())
        assertEquals(2, timeRange.ranges.size)
        assertEquals(12000..13000, timeRange.ranges[0])
        assertEquals(TimeRange.ranges["night"]!!.ranges[0], timeRange.ranges[1])
    }
}