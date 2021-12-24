package com.cablemc.pokemoncobbled.common.command

import com.cablemc.pokemoncobbled.mod.PokemonCobbledMod
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

object ShowdownWriteCommand {

    fun register(dispatcher : CommandDispatcher<CommandSourceStack>) {
        val command = Commands.literal("sdwrite")
            .requires { it.hasPermission(4) }
            .then(Commands.argument("line", StringArgumentType.greedyString()).executes { execute(it) })
        dispatcher.register(command)
    }

    private fun execute(context: CommandContext<CommandSourceStack>) : Int {
        val lineToExecute = StringArgumentType.getString(context, "line")
        PokemonCobbledMod.showdown.write(lineToExecute)
        return Command.SINGLE_SUCCESS
    }

}