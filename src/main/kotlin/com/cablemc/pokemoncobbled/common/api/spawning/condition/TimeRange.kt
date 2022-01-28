package com.cablemc.pokemoncobbled.common.api.spawning.condition

/**
 * @since January 26th, 2022
 */
class TimeRange() {
    companion object {
        val ranges = mutableMapOf(
            "day" to TimeRange(0..12000),
            "night" to TimeRange(12000..23999),
            "noon" to TimeRange(5000..7000),
            "midnight" to TimeRange(17000..19000),
            "dawn" to TimeRange(23000..23999, 0..1000),
            "dusk" to TimeRange(11000..13000),
            "twilight" to TimeRange(22000..23999, 12000..14000),
            "morning" to TimeRange(0..4999),
            "afternoon" to TimeRange(7000..12000)
        )
    }

    constructor(vararg ranges: IntRange) : this() {
        this.ranges = ranges.toMutableList()
    }

    var ranges = mutableListOf<IntRange>()

    fun contains(timeTicks: Int) = ranges.any { timeTicks in it }
}