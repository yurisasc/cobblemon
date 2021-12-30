package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.command.GivePokemon
import com.cablemc.pokemoncobbled.common.command.PokeSpawn
import com.cablemc.pokemoncobbled.common.command.TestCommand
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object CommandRegistrar {
    @SubscribeEvent
    fun on(event: RegisterCommandsEvent) {
        PokeSpawn.register(event.dispatcher)
        GivePokemon.register(event.dispatcher)
        TestCommand.register(event.dispatcher)
    }
}