package com.cobblemon.mod.common.command

import com.cobblemon.mod.common.api.permission.CobblemonPermissions
import com.cobblemon.mod.common.api.text.suggest
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.util.permission
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

object PokedexCommand {

    private const val NAME = "pokedex"
    fun register(dispatcher : CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal(PokedexCommand.NAME)
                .permission(CobblemonPermissions.PC)
                .executes(this::execute)
        )
    }

    private fun execute(context: CommandContext<ServerCommandSource>): Int {
        val player = context.source.playerOrThrow
        player.sendMessage("@TODO".text())
        return Command.SINGLE_SUCCESS
    }
}