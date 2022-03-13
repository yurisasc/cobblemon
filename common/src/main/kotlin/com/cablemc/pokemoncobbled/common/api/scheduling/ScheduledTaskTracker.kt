package com.cablemc.pokemoncobbled.common.api.scheduling

object ScheduledTaskTracker {
    private val tasks = mutableListOf<ScheduledTask>()

    fun clear() {
        tasks.clear()
    }

    fun update() {
        for (task in tasks.toList()) {
            task.update()
            if (task.expired) {
                tasks.remove(task)
            }
        }
    }

    fun addTask(task: ScheduledTask) {
        tasks.add(task)
    }
}