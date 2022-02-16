package com.cablemc.pokemoncobbled.forge.client.listener

import com.cablemc.pokemoncobbled.forge.common.api.scheduling.ScheduledTaskTracker
import com.cablemc.pokemoncobbled.forge.common.util.ifClient
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.ClientPlayerNetworkEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.LogicalSide

object ClientSchedulingListener {
    @SubscribeEvent
    fun on(event: ClientPlayerNetworkEvent.LoggedOutEvent) {
        // Reinstate
        //ScheduledTaskTracker.clear()
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun on(event: TickEvent.PlayerTickEvent) {
        ifClient {
            if (event.player.uuid == Minecraft.getInstance().player?.uuid && event.phase == TickEvent.Phase.START && event.side == LogicalSide.CLIENT) {
                ScheduledTaskTracker.tick()
            }
        }
    }
}