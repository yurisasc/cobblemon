package com.cablemc.pokemoncobbled.forge.common.api.scheduling

import com.cablemc.pokemoncobbled.forge.common.util.ifServer
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.SubscribeEvent

object ScheduledTaskListener {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun on(event: TickEvent.ServerTickEvent) {
        ifServer {
            if (event.phase == TickEvent.Phase.START) {
                ScheduledTaskTracker.tick()
            }
        }
    }
}