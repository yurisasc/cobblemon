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
import com.cablemc.pokemoncobbled.common.util.*
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.command.CommandManager.literal

object HealPokemonCommand {

    fun register(dispatcher : CommandDispatcher<ServerCommandSource>) {
        val command = dispatcher.register(literal("healpokemon")
            .permission(CobbledPermissions.HEAL_POKEMON)
            .permissionLevel(PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS)
            .then(
                CommandManager.argument("player", EntityArgumentType.player())
                    .permission(CobbledPermissions.HEAL_POKEMON_OTHER)
                    .permissionLevel(PermissionLevel.MULTIPLAYER_MANAGEMENT)
                    .executes { execute(it) }
            ))
        dispatcher.register(literal("pokeheal").redirect(command))
    }

    private fun execute(context: CommandContext<ServerCommandSource>) : Int {
        val entity = context.source.entity
        val player = context.player("player") ?: (if (entity is ServerPlayerEntity) entity else return 0)
        if (!player.world.isClient) {
            val party = player.party()
            party.heal()
            context.source.sendFeedback(commandLang("healpokemon.heal", player.name), true)
        }
        return Command.SINGLE_SUCCESS
    }

}