/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.command

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.permission.CobblemonPermissions
import com.cobblemon.mod.common.battles.BattleBuilder
import com.cobblemon.mod.common.util.permission
import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource

object RandomBattleCommand {

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
                literal("randombattle")
                        .permission(CobblemonPermissions.RANDOM_BATTLE)
                        .then(
                                argument("player", EntityArgumentType.player())
                                        .executes { execute(it, 50, "random") } // Defaults when neither integer nor string is provided
                                        .then(
                                                argument("number", IntegerArgumentType.integer())
                                                        .executes { execute(it, "random") } // Default string when only number is provided
                                                        .then(
                                                                argument("type", StringArgumentType.string())
                                                                        .executes { execute(it) }
                                                        )
                                        )
                        )
        )
    }

    private fun execute(context: CommandContext<ServerCommandSource>): Int {
        val player = EntityArgumentType.getPlayer(context, "player")
        val number = IntegerArgumentType.getInteger(context, "number")
        val type = StringArgumentType.getString(context, "type")

        // Your command logic here using 'player', 'number', and 'stringArg'
        BattleBuilder.pvc(player,"strong", number, type, true)
        return SINGLE_SUCCESS
    }

    // if no type is given to the command after the integer for level
    private fun execute(context: CommandContext<ServerCommandSource>, defaultString: String): Int {
        val player = EntityArgumentType.getPlayer(context, "player")
        val number = IntegerArgumentType.getInteger(context, "number")

        // Use default string
        val type = defaultString

        // Your command logic here using 'player', 'number', and 'stringArg'
        BattleBuilder.pvc(player,"strong", number, type, true)

        return SINGLE_SUCCESS
    }

    // if no additional arguments are provided after player
    private fun execute(context: CommandContext<ServerCommandSource>, defaultNumber: Int, defaultString: String): Int {
        val player = EntityArgumentType.getPlayer(context, "player")
        val number = defaultNumber
        val type = defaultString

        // Your command logic here using 'player', 'number', and 'stringArg'
        BattleBuilder.pvc(player,"strong", number, type, true)

        return SINGLE_SUCCESS
    }

}