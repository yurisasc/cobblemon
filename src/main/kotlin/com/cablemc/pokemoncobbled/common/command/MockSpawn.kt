package com.cablemc.pokemoncobbled.common.command

import com.cablemc.pokemoncobbled.common.spawning.SpawningTest
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.Util
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.TextComponent

/**
 * Command for spawning a random Pokémon near you
 */
object MockSpawn {
    fun register(dispatcher : CommandDispatcher<CommandSourceStack>) {
        val command = Commands.literal("mockspawn")
            .requires { it.hasPermission(4) }
            .executes { execute(it) }
        dispatcher.register(command)
    }

    private fun execute(context: CommandContext<CommandSourceStack>) : Int {

        try {
            SpawningTest.trySpawn(context.source.playerOrException)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return Command.SINGLE_SUCCESS
    }
}