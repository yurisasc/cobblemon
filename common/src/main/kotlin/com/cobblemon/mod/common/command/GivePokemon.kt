/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.command

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.permission.CobblemonPermissions
import com.cobblemon.mod.common.api.permission.PermissionLevel
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.command.argument.PokemonPropertiesArgumentType
import com.cobblemon.mod.common.util.appendRequirement
import com.cobblemon.mod.common.util.commandLang
import com.cobblemon.mod.common.util.permission
import com.cobblemon.mod.common.util.permissionLevel
import com.cobblemon.mod.common.util.player
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity

object GivePokemon {
    fun register(dispatcher : CommandDispatcher<ServerCommandSource>) {
        val command = dispatcher.register(literal("givepokemon")
            .permission(CobblemonPermissions.GIVE_POKEMON)
            .permissionLevel(PermissionLevel.MULTIPLAYER_MANAGEMENT)
            .then(
                CommandManager.argument("pokemon", PokemonPropertiesArgumentType.properties())
                    .appendRequirement { it.player != null }
                    .executes { execute(it, it.source.playerOrThrow) }
            )
            .then(
                CommandManager.argument("player", EntityArgumentType.player())
                    .then(CommandManager.argument("pokemon", PokemonPropertiesArgumentType.properties())
                        .executes { execute(it, it.player()) }
                    )
            ))
        dispatcher.register(literal("pokegive").redirect(command))
    }

    private fun execute(context: CommandContext<ServerCommandSource>, player: ServerPlayerEntity) : Int {
        try {
            val pokemonProperties = PokemonPropertiesArgumentType.getPokemonProperties(context, "pokemon")

            if (pokemonProperties.species == null) {
                player.sendMessage(commandLang("givepokemon.nospecies").red())
                return Command.SINGLE_SUCCESS
            }

            val pokemon = pokemonProperties.create()
            val party = Cobblemon.storage.getParty(player)
            party.add(pokemon)
            context.source.sendFeedback(commandLang("givepokemon.give", pokemon.species.translatedName, player.name), true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Command.SINGLE_SUCCESS
    }
}