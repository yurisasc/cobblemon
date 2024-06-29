/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.command

import com.cobblemon.mod.common.api.permission.CobblemonPermissions
import com.cobblemon.mod.common.battles.BattleRegistry
import com.cobblemon.mod.common.util.permission
import com.cobblemon.mod.common.util.player
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.commands.Commands
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.level.ServerPlayer

object StopBattleCommand {

    fun register(dispatcher : CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(Commands.literal("stopbattle")
            .permission(CobblemonPermissions.STOP_BATTLE)
            .then(
                Commands.argument("player", EntityArgumentType.player())
                    .executes(::execute)
            ))
    }

    private fun execute(context: CommandContext<ServerCommandSource>) : Int {
        val entity = context.source.entity
        val player = context.player("player") ?: (if (entity is ServerPlayer) entity else return 0)
        if (!player.level().isClient) {
            val battle = BattleRegistry.getBattleByParticipatingPlayer(player) ?: return 0
            battle.stop()
        }
        return Command.SINGLE_SUCCESS
    }

}