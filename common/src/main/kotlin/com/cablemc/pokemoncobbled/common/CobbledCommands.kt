package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.command.*
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
        LevelUp.register(dispatcher)
    }
}