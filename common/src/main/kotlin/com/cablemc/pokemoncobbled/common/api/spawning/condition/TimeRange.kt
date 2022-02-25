package com.cablemc.pokemoncobbled.common.api.spawning.condition

/**
 * A range of time ticks for a world to be used in [SpawningCondition]s. A time range
 * is defined as any number of tick ranges. For example, you can define a time range
 * as being between 0 and 12000 ticks or 13000 and 14000 ticks just for a single time
 * range. This is to deal with the limitation that Minecraft days go from 0 ticks to
 * 24000 ticks before looping back to 0.
 *
 * There are a series of in-built time ranges that you can replace if they don't suit
 * your definitions.
 *
 * If you want a time range to be referenced by name in [SpawningCondition]s, you need
 * to register it, by name, in [ranges].
 *
 * @author Hiroku
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