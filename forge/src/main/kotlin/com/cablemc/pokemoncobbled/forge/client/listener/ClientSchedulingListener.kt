package com.cablemc.pokemoncobbled.forge.client.listener

import com.cablemc.pokemoncobbled.common.api.scheduling.ScheduledTaskTracker
import com.cablemc.pokemoncobbled.common.util.ifClient
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.ClientPlayerNetworkEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.Priority
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.server.ServerLifecycleHooks

object ClientSchedulingListener {
    @SubscribeEvent
    fun on(event: ClientPlayerNetworkEvent.LoggedOutEvent) {
        ScheduledTaskTracker.clear()
    }

    @SubscribeEvent(priority = Priority.HIGHEST)
    fun on(event: TickEvent.PlayerTickEvent) {
        ifClient {
            fun getServer() = ServerLifecycleHooks.getCurrentServer()
            if (event.player.uuid == Minecraft.getInstance().player?.uuid && event.phase == TickEvent.Phase.START && event.side == LogicalSide.CLIENT) {
                ScheduledTaskTracker.tick()
            }
        }
    }
}