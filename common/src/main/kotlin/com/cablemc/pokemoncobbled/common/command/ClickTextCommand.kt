package com.cablemc.pokemoncobbled.common.command

import com.cablemc.pokemoncobbled.common.api.text.textClickHandlers
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.server.level.ServerPlayerEntity
import java.util.UUID

object ClickTextCommand {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("cobbledclicktext")
                .requires { src -> src.entity is ServerPlayerEntity }
                .then(
                    RequiredArgumentBuilder
                        .argument<CommandSourceStack, String>("callback", StringArgumentType.greedyString())
                        .executes { ctx ->
                            val player = ctx.source.entity as ServerPlayerEntity
                            textClickHandlers[UUID.fromString(ctx.getArgument("callback", String::class.java))]?.invoke(player)
                            return@executes 1
                        }
                )
        )
    }
}