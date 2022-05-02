package com.cablemc.pokemoncobbled.common.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity

object KillShoulderCommand {

    fun register(dispatcher : CommandDispatcher<ServerCommandSource>) {
        val command = CommandManager.literal("killshoulder")
            .requires { it.hasPermissionLevel(4) }
            .executes { execute(it) }
        dispatcher.register(command)
    }

    private fun execute(context: CommandContext<ServerCommandSource>) : Int {
        val player = context.source.entity as? ServerPlayerEntity ?: return Command.SINGLE_SUCCESS
        player.shoulderEntityLeft = NbtCompound()
        player.shoulderEntityRight = NbtCompound()
        return Command.SINGLE_SUCCESS
    }

}