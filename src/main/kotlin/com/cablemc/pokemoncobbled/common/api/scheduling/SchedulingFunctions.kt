package com.cablemc.pokemoncobbled.common.api.scheduling

import net.minecraft.util.Mth.ceil

fun after(ticks: Int = 0, seconds: Float = 0F, action: () -> Unit) {
    ScheduledTaskTracker.addTask(
        ScheduledTask(
            action = { action() },
            delayTicks = ticks + ceil(seconds * 20)
        )
    )
}

fun taskBuilder() = ScheduledTask.Builder()