package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.command.PokeSpawn
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object CommandRegistrar {
    @SubscribeEvent
    fun on(event: RegisterCommandsEvent) {
        PokeSpawn.register(event.dispatcher)
    }
}