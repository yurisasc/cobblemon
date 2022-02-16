package com.cablemc.pokemoncobbled.forge.common

import com.cablemc.pokemoncobbled.forge.common.command.GivePokemon
import com.cablemc.pokemoncobbled.forge.common.command.SpawnPokemon
import com.cablemc.pokemoncobbled.forge.common.command.ChangeScaleAndSize
import com.cablemc.pokemoncobbled.forge.common.command.TestCommand
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object CommandRegistrar {
    @SubscribeEvent
    fun on(event: RegisterCommandsEvent) {
        SpawnPokemon.register(event.dispatcher)
        GivePokemon.register(event.dispatcher)
        ChangeScaleAndSize.register(event.dispatcher)
        TestCommand.register(event.dispatcher)
    }
}