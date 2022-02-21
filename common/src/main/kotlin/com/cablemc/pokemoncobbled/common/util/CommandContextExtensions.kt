package com.cablemc.pokemoncobbled.common.util

import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.server.level.ServerPlayer

fun CommandContext<CommandSourceStack>.player(argumentName: String = "player"): ServerPlayer = EntityArgument.getPlayer(this, argumentName)