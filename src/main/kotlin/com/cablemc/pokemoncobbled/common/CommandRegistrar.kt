package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.command.ShowdownWriteCommand
import com.cablemc.pokemoncobbled.common.command.GivePokemon
import com.cablemc.pokemoncobbled.common.command.SpawnPokemon
import com.cablemc.pokemoncobbled.common.command.ShowdownReadCommand
import com.cablemc.pokemoncobbled.common.command.ChangeScaleAndSize
import com.cablemc.pokemoncobbled.common.command.TestCommand
import com.cablemc.pokemoncobbled.common.command.*
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object CommandRegistrar {
    @SubscribeEvent
    fun on(event: RegisterCommandsEvent) {
        SpawnPokemon.register(event.dispatcher)
        GivePokemon.register(event.dispatcher)
        ShowdownWriteCommand.register(event.dispatcher)
        ShowdownReadCommand.register(event.dispatcher)
        ChangeScaleAndSize.register(event.dispatcher)
        TestCommand.register(event.dispatcher)
        MoveCommand.register(event.dispatcher)
        SwitchCommand.register(event.dispatcher)
    }
}