package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.command.ChangeScaleAndSize
import com.cablemc.pokemoncobbled.common.command.ClickTextCommand
import com.cablemc.pokemoncobbled.common.command.GivePokemon
import com.cablemc.pokemoncobbled.common.command.LevelUp
import com.cablemc.pokemoncobbled.common.command.SpawnPokemon
import com.cablemc.pokemoncobbled.common.command.TakePokemon
import com.cablemc.pokemoncobbled.common.command.TestCommand
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

object CobbledCommands {
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>, selection: CommandManager.RegistrationEnvironment) {
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