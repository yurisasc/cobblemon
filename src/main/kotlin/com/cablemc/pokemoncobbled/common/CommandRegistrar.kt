package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.command.*
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object CommandRegistrar {
    @SubscribeEvent
    fun on(event: RegisterCommandsEvent) {
        PokeSpawn.register(event.dispatcher)
        GivePokemon.register(event.dispatcher)
        ChangeScaleAndSize.register(event.dispatcher)
        TestCommand.register(event.dispatcher)
    }
}