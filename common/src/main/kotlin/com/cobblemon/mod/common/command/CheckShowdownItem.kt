package com.cobblemon.mod.common.command

import com.cobblemon.mod.common.api.pokemon.helditem.HeldItemProvider
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

object CheckShowdownItem {
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("checkshowdownitem")
            .requires { it.player != null }
            .executes {execute(it.source.playerOrThrow)}
        )
    }

    fun execute(player: ServerPlayerEntity) : Int {
        player.sendMessage(Text.literal(HeldItemProvider.provideShowdownId(player.mainHandStack) ?: "none"))
        return Command.SINGLE_SUCCESS
    }
}