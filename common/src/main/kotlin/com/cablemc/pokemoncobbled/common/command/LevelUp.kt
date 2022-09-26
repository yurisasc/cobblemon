/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.command

import com.cablemc.pokemoncobbled.common.api.permission.CobbledPermissions
import com.cablemc.pokemoncobbled.common.api.permission.PermissionLevel
import com.cablemc.pokemoncobbled.common.api.pokemon.experience.CommandExperienceSource
import com.cablemc.pokemoncobbled.common.api.text.text
import com.cablemc.pokemoncobbled.common.util.party
import com.cablemc.pokemoncobbled.common.util.permission
import com.cablemc.pokemoncobbled.common.util.permissionLevel
import com.cablemc.pokemoncobbled.common.util.player
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity

object LevelUp {

    fun register(dispatcher : CommandDispatcher<ServerCommandSource>) {
        val command = CommandManager.literal("levelup")
            .permission(CobbledPermissions.LEVEL_UP_SELF)
            .permissionLevel(PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS)
            .then(
                CommandManager.argument("player", EntityArgumentType.player())
                    .permission(CobbledPermissions.LEVEL_UP_OTHER)
                    .permissionLevel(PermissionLevel.MULTIPLAYER_MANAGEMENT)
                    .then(
                        CommandManager.argument("slot", IntegerArgumentType.integer(1, 99))
                            .executes { execute(it, it.player()) }
                    )
            )
            .then(
                CommandManager.argument("slot", IntegerArgumentType.integer(1, 99))
                    .requires { it.entity is ServerPlayerEntity && it.player != null }
                    .executes { execute(it, it.source.playerOrThrow) }
            )

        dispatcher.register(command)
    }

    private fun execute(context: CommandContext<ServerCommandSource>, player: ServerPlayerEntity) : Int {
        val slot = IntegerArgumentType.getInteger(context, "slot")
        val party = player.party()
        if (slot > party.size()) {
            // todo translate
            context.source.sendError("Your party only has ${party.size()} slots.".text())
            return 0
        }

        val pokemon = party.get(slot - 1)
        if (pokemon == null) {
            context.source.sendError("There is no Pokémon in slot $slot".text())
            return 0
        }

        val source = CommandExperienceSource(context.source)
        pokemon.addExperienceWithPlayer(player, source, pokemon.getExperienceToNextLevel())
        return Command.SINGLE_SUCCESS
    }
}