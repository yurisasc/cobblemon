package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.command.ChangeScaleAndSize
import com.cablemc.pokemoncobbled.common.command.ClickTextCommand
import com.cablemc.pokemoncobbled.common.command.GivePokemon
import com.cablemc.pokemoncobbled.common.command.LevelUp
import com.cablemc.pokemoncobbled.common.command.SpawnPokemon
import com.cablemc.pokemoncobbled.common.command.TakePokemon
import com.cablemc.pokemoncobbled.common.command.TestCommand
import com.cablemc.pokemoncobbled.common.command.*
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

object CobbledCommands {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>, selection: Commands.CommandSelection) {
        SpawnPokemon.register(dispatcher)
        GivePokemon.register(dispatcher)
        TakePokemon.register(dispatcher)
        ChangeScaleAndSize.register(dispatcher)
        TestCommand.register(dispatcher)
        ClickTextCommand.register(dispatcher)
        PokemonEditCommand.register(dispatcher)
        LevelUp.register(dispatcher)
    }
}