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
                        .executes { execute(it,) }
                )
        )
    }

    private fun execute(context: CommandContext<ServerCommandSource>) : Int {
        // todo use random numbers to randomly generate a team of 6 pokemon

        // todo forEach pokemon get list of 4 moves at random for each of them and apply them to that pokemon

        // todo start battle with player entity's party and generate AI actor to battle against
        val player = EntityArgumentType.getPlayer(context, "player")

        val playerData = Cobblemon.playerData.get(player)

        // Start Showdown Battle with following arguments
        BattleBuilder.pvc(player, "strong")

        return SINGLE_SUCCESS
    }

}