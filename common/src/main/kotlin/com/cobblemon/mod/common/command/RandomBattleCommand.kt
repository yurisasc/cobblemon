/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.command

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

    // todo add param before number or type for AI subtypes

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
                literal("randombattle")
                        .permission(CobblemonPermissions.RANDOM_BATTLE)
                        .then(
                                argument("player", EntityArgumentType.player())
                                        .then(
                                                argument("aiType", StringArgumentType.string())
                                                        .then(
                                                                argument("number", IntegerArgumentType.integer())
                                                                        .executes { executeNumber(it, 50, "random") } // If only number is provided after aiType
                                                                        .then(
                                                                                argument("type", StringArgumentType.string())
                                                                                        .executes { executeWithNumberType(it) } // If number and type are provided after aiType
                                                                        )
                                                        )
                                                        .then(
                                                                argument("type", StringArgumentType.string())
                                                                        .executes { executeType(it, "random", 50) } // If only type is provided after aiType
                                                                        .then(
                                                                                argument("number", IntegerArgumentType.integer())
                                                                                        .executes { executeWithTypeNumber(it) } // If type and number are provided after aiType
                                                                        )
                                                        )
                                                        .executes { executeAITypeDefault(it, 50, "random") } // Default if neither number nor type is provided after aiType
                                        )
                                        .executes { executeDefault(it, "random", 50, "random") } // Default if aiType is not provided
                        )
        )
    }

    // NUMBER and TYPE
    private fun executeWithNumberType(context: CommandContext<ServerCommandSource>): Int {
        val player = EntityArgumentType.getPlayer(context, "player")
        val aiType = StringArgumentType.getString(context,"aiType").lowercase()
        val number = IntegerArgumentType.getInteger(context, "number")
        val type = StringArgumentType.getString(context, "type")

        // Your command logic here using 'player', 'number', and 'stringArg'
        BattleBuilder.pvc(player, aiType, number, type, true)
        return SINGLE_SUCCESS
    }

    // TYPE and NUMBER
    private fun executeWithTypeNumber(context: CommandContext<ServerCommandSource>): Int {
        val player = EntityArgumentType.getPlayer(context, "player")
        val aiType = StringArgumentType.getString(context,"aiType").lowercase()
        val number = IntegerArgumentType.getInteger(context, "number")
        val type = StringArgumentType.getString(context, "type")

        // Your command logic here using 'player', 'number', and 'stringArg'
        BattleBuilder.pvc(player,aiType, number, type, true)
        return SINGLE_SUCCESS
    }

    // if no type is given to the command after the integer for level NUMBER
    private fun executeNumber(context: CommandContext<ServerCommandSource>, defaultNumber: Int, defaultString: String): Int {
        val player = EntityArgumentType.getPlayer(context, "player")
        val aiType = StringArgumentType.getString(context,"aiType").lowercase()
        val number = IntegerArgumentType.getInteger(context, "number")

        // Use default string
        val type = defaultString

        // Your command logic here using 'player', 'number', and 'stringArg'
        BattleBuilder.pvc(player, aiType, number, type, true)

        return SINGLE_SUCCESS
    }

    // if no type is given to the command after the integer for level NUMBER
    private fun executeType(context: CommandContext<ServerCommandSource>, defaultString: String, defaultNumber: Int): Int {
        val player = EntityArgumentType.getPlayer(context, "player")
        val aiType = StringArgumentType.getString(context,"aiType").lowercase()
        val type = StringArgumentType.getString(context, "type")

        // Use default string
        val number = defaultNumber

        // Your command logic here using 'player', 'number', and 'stringArg'
        BattleBuilder.pvc(player, aiType, number, type, true)

        return SINGLE_SUCCESS
    }

    // if no additional arguments are provided after player DEFAULT
    private fun executeDefault(context: CommandContext<ServerCommandSource>, defaultAIType: String, defaultNumber: Int, defaultString: String): Int {
        val player = EntityArgumentType.getPlayer(context, "player")
        val aiType = defaultAIType
        val number = defaultNumber
        val type = defaultString

        // Your command logic here using 'player', 'number', and 'stringArg'
        BattleBuilder.pvc(player, aiType, number, type, true)

        return SINGLE_SUCCESS
    }

    // DEFAULT WITH AI Type
    private fun executeAITypeDefault(context: CommandContext<ServerCommandSource>, defaultNumber: Int, defaultString: String): Int {
        val player = EntityArgumentType.getPlayer(context, "player")
        val aiType = StringArgumentType.getString(context,"aiType").lowercase()
        val number = defaultNumber
        val type = defaultString

        // Your command logic here using 'player', 'number', and 'stringArg'
        BattleBuilder.pvc(player, aiType, number, type, true)

        return SINGLE_SUCCESS
    }

}