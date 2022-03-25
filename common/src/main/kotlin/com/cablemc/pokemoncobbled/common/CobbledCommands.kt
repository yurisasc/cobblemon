package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.command.*
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

object CobbledCommands {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>, selection: Commands.CommandSelection) {
        SpawnPokemon.register(dispatcher)
        GivePokemon.register(dispatcher)
        ChangeScaleAndSize.register(dispatcher)
        TestCommand.register(dispatcher)
        ClickTextCommand.register(dispatcher)
        PokemonEditCommand.register(dispatcher)
    }
}