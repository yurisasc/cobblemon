package com.cablemc.pokemoncobbled.common.api.scheduling

import net.minecraft.util.Mth.ceil
import kotlin.math.roundToInt

fun after(ticks: Int = 0, seconds: Float = 0F, action: () -> Unit) {
    ScheduledTaskTracker.addTask(
        ScheduledTask(
            action = { action() },
            delayTicks = ticks + ceil(seconds * 20)
        )
    )
}

fun lerp(ticks: Int, action: (Float) -> Unit) {
    var tick = 0F
    action(tick / ticks)
    taskBuilder().interval(1).iterations(Int.MAX_VALUE).execute { task ->
        tick++
        action(tick / ticks)
        if (tick >= ticks) {
            task.expire()
        }
    }
}

fun lerp(seconds: Float, action: (Float) -> Unit) = lerp(ticks = (seconds * 20F).roundToInt(), action)


fun taskBuilder() = ScheduledTask.Builder()