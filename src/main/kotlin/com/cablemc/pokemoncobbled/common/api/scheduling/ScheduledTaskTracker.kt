package com.cablemc.pokemoncobbled.common.api.scheduling

object ScheduledTaskTracker {
    private val tasks = mutableListOf<ScheduledTask>()

    fun clear() {
        tasks.clear()
    }

    fun tick() {
        for (task in tasks.toList()) {
            task.tick()
            if (task.expired) {
                tasks.remove(task)
            }
        }
    }

    fun addTask(task: ScheduledTask) {
        this.tasks.add(task)
    }
}