package com.cablemc.pokemoncobbled.common.util

import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity

fun CommandContext<ServerCommandSource>.player(argumentName: String = "player") = EntityArgumentType.getPlayer(this, argumentName)