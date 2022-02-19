package com.cablemc.pokemoncobbled.common.client

import com.cablemc.pokemoncobbled.common.api.scheduling.ScheduledTaskTracker

object ClientSchedulingListener {
    fun onLogout() {
        ScheduledTaskTracker.clear()
    }

    fun onRender() {
        ScheduledTaskTracker.tick()
    }
}