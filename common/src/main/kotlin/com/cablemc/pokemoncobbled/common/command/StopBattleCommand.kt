/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.command

import com.cablemc.pokemoncobbled.common.battles.BattleRegistry
import com.cablemc.pokemoncobbled.common.util.player
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity

object StopBattleCommand {

    fun register(dispatcher : CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("stopbattle")
            .requires { it.hasPermissionLevel(4) }
            .then(
                CommandManager.argument("player", EntityArgumentType.player())
                    .executes { execute(it) }
            ))
    }

    private fun execute(context: CommandContext<ServerCommandSource>) : Int {
        val entity = context.source.entity
        val player = context.player("player") ?: (if (entity is ServerPlayerEntity) entity else return 0)
        if (!player.world.isClient) {
            val battle = BattleRegistry.getBattleByParticipatingPlayer(player) ?: return 0
            battle.stop()
        }
        return Command.SINGLE_SUCCESS
    }

}