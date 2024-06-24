/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.command

import com.cobblemon.mod.common.api.permission.CobblemonPermissions
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.api.text.suggest
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.entity.npc.NPCEntity
import com.cobblemon.mod.common.util.commandLang
import com.cobblemon.mod.common.util.requiresWithPermission
import com.cobblemon.mod.common.util.traceFirstEntityCollision
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.entity.LivingEntity
import net.minecraft.nbt.visitor.NbtOrderedStringFormatter
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand

object NPCEditCommand {
    fun register(dispatcher : CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("npcedit")
            .requiresWithPermission(CobblemonPermissions.NPC_EDIT) { it.player != null }
            .executes { execute(it, it.source.playerOrThrow) })
    }

    private fun execute(context: CommandContext<ServerCommandSource>, player: ServerPlayerEntity) : Int {
        val targetEntity = player.traceFirstEntityCollision(entityClass = NPCEntity::class.java)
        if (targetEntity == null) {
            player.sendMessage(commandLang("npcedit.non_npc").red())
            return 0
        }

        targetEntity.edit(player)
        return Command.SINGLE_SUCCESS
    }
}