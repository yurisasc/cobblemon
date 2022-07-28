package com.cablemc.pokemoncobbled.common.command

import com.cablemc.pokemoncobbled.common.api.text.suggest
import com.cablemc.pokemoncobbled.common.api.text.text
import com.cablemc.pokemoncobbled.common.util.sendServerMessage
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.nbt.visitor.NbtOrderedStringFormatter
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand

object GetNBT {
    fun register(dispatcher : CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("getnbt")
            .requires { it.hasPermissionLevel(4) }
            .requires { it != it.server}
            .executes { execute(it, it.source.player) })
    }

    private fun execute(context: CommandContext<ServerCommandSource>, player: ServerPlayerEntity) : Int {
        val stack = player.getStackInHand(Hand.MAIN_HAND)
        try {
            val formatter = NbtOrderedStringFormatter("", 0, mutableListOf())
            val str = formatter.apply(stack.nbt)
            player.sendServerMessage(str.text().suggest(str))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Command.SINGLE_SUCCESS
    }
}