package com.cablemc.pokemoncobbled.common.command

import com.cablemc.pokemoncobbled.client.PokemonCobbledClient
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

object TestCommand {

    fun register(dispatcher : CommandDispatcher<CommandSourceStack>) {
        val command = Commands.literal("testcommand")
            .requires { it.hasPermission(4) }
            .executes { execute(it) }
        dispatcher.register(command)
    }

    private fun execute(context: CommandContext<CommandSourceStack>): Int {

        val pkm = PokemonCobbledClient.storage.myParty.get(0)
        pkm?.moveSet?.getMoves()?.forEach {
            println("Move ${it.name}")
        }
        println("Test2 ${pkm?.moveSet?.moves?.get(1)?.name}")
        println("Level ${pkm?.level}")

        return Command.SINGLE_SUCCESS
    }

}