package com.cablemc.pokemoncobbled.common.command

import com.cablemc.pokemoncobbled.mod.PokemonCobbledMod
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.Util
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.TextComponent

object ShowdownReadCommand {

    fun register(dispatcher : CommandDispatcher<CommandSourceStack>) {
        val command = Commands.literal("sdread")
            .requires { it.hasPermission(4) }
            .executes { execute(it) }
        dispatcher.register(command)
    }

    private fun execute(context: CommandContext<CommandSourceStack>) : Int {
        val input = PokemonCobbledMod.showdown.read()
        context.source.playerOrException.sendMessage(TextComponent(input ?: "No input"), Util.NIL_UUID)
        println(input ?: "No input")
        return Command.SINGLE_SUCCESS
    }

}