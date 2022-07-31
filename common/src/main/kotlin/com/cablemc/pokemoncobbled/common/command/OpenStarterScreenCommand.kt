package com.cablemc.pokemoncobbled.common.command

import com.cablemc.pokemoncobbled.common.CobbledNetwork.sendPacket
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.net.messages.client.ui.StarterUIPacket
import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.command.CommandManager.literal

object OpenStarterScreenCommand {

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val command = dispatcher.register(
            literal("openstarterscreen")
                .requires { it.hasPermissionLevel(4) }
                .then(
                    argument("player", EntityArgumentType.player())
                        .executes { execute(it,) }
                )
        )
    }

    private fun execute(context: CommandContext<ServerCommandSource>) : Int {
        val player = EntityArgumentType.getPlayer(context, "player")

        player.sendPacket(StarterUIPacket(PokemonCobbled.config.starters))
        return SINGLE_SUCCESS
    }

}