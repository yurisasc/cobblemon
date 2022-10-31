/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.command

import com.cablemc.pokemod.common.api.permission.PermissionLevel
import com.cablemc.pokemod.common.api.permission.PokemodPermissions
import com.cablemc.pokemod.common.util.commandLang
import com.cablemc.pokemod.common.util.party
import com.cablemc.pokemod.common.util.permission
import com.cablemc.pokemod.common.util.permissionLevel
import com.cablemc.pokemod.common.util.player
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.entity.Entity
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity

object HealPokemonCommand {

    fun register(dispatcher : CommandDispatcher<ServerCommandSource>) {
        val command = dispatcher.register(literal("healpokemon")
            .permission(PokemodPermissions.HEAL_POKEMON_SELF)
            .permissionLevel(PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS)
            .executes { execute(it.source, it.source.playerOrThrow) }
            .then(
                CommandManager.argument("player", EntityArgumentType.player())
                    .permission(PokemodPermissions.HEAL_POKEMON_OTHER)
                    .permissionLevel(PermissionLevel.MULTIPLAYER_MANAGEMENT)
                    .executes { execute(it.source, it.player("player")) }
            ))
        dispatcher.register(literal("pokeheal")
            .redirect(command)
            .executes(command.command)
            .permission(PokemodPermissions.HEAL_POKEMON_SELF)
            .permissionLevel(PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS)
        )
    }

    private fun execute(source: ServerCommandSource, target: ServerPlayerEntity) : Int {
        if (!target.world.isClient) {
            val party = target.party()
            party.heal()
            source.sendFeedback(commandLang("healpokemon.heal", target.name), true)
        }
        return Command.SINGLE_SUCCESS
    }

}