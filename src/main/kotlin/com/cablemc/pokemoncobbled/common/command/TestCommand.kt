package com.cablemc.pokemoncobbled.common.command

import com.cablemc.pokemoncobbled.common.api.storage.PokemonStoreManager
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.server.level.ServerPlayer

object TestCommand {

    fun register(dispatcher : CommandDispatcher<CommandSourceStack>) {
        val command = Commands.literal("testcommand")
            .requires { it.hasPermission(4) }
            .executes { execute(it) }
        dispatcher.register(command)
    }

    private fun execute(context: CommandContext<CommandSourceStack>): Int {
        if(context.source.entity !is ServerPlayer) {
            return Command.SINGLE_SUCCESS
        }
        val player: ServerPlayer = context.source.entity as ServerPlayer
        val pkm = PokemonStoreManager.getParty(player).get(0)
        pkm?.moveSet?.getMoves()?.forEach {
            println("Move ${it.name}")
        }
        return Command.SINGLE_SUCCESS
    }

}