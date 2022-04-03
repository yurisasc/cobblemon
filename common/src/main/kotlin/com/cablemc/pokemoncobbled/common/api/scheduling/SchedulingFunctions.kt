package com.cablemc.pokemoncobbled.common.api.scheduling

fun after(ticks: Int = 0, seconds: Float = 0F, action: () -> Unit) {
    ScheduledTaskTracker.addTask(
        ScheduledTask(
            action = { action() },
            delaySeconds = ticks / 20F + seconds
        )
    )
}

fun lerp(seconds: Float = 0F, action: (Float) -> Unit) {
    val startedTime = System.currentTimeMillis()
    var passed = 0F
    if (seconds == 0F) {
        action(1F)
        return
    }
    action(passed / seconds)
    if (passed / seconds != 1F) {
        taskBuilder().interval(0F).iterations(-1).execute { task ->
            passed = (System.currentTimeMillis() - startedTime)/1000F
            if (passed > seconds) {
                passed = seconds
            }
            action(passed / seconds)
            if (passed >= seconds) {
                task.expire()
            }
        }.build()
    }
}

fun taskBuilder() = ScheduledTask.Builder()