/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.command

import com.cablemc.pokemod.common.Pokemod
import com.cablemc.pokemod.common.api.permission.PermissionLevel
import com.cablemc.pokemod.common.api.permission.PokemodPermissions
import com.cablemc.pokemod.common.api.text.red
import com.cablemc.pokemod.common.command.argument.PokemonPropertiesArgumentType
import com.cablemc.pokemod.common.util.appendRequirement
import com.cablemc.pokemod.common.util.commandLang
import com.cablemc.pokemod.common.util.permission
import com.cablemc.pokemod.common.util.permissionLevel
import com.cablemc.pokemod.common.util.player
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
            .permission(PokemodPermissions.GIVE_POKEMON)
            .permissionLevel(PermissionLevel.MULTIPLAYER_MANAGEMENT)
            .then(
                CommandManager.argument("player", EntityArgumentType.player())
                    .then(CommandManager.argument("pokemon", PokemonPropertiesArgumentType.properties())
                        .executes { execute(it) }
                    )
            ))
        dispatcher.register(literal("pokegive").redirect(command))
    }

    private fun execute(context: CommandContext<ServerCommandSource>): Int {
        try {
            val player = context.player()
            val pokemonProperties = PokemonPropertiesArgumentType.getPokemonProperties(context, "pokemon")
            if (pokemonProperties.species == null) {
                player.sendMessage(commandLang("givepokemon.nospecies").red())
                return Command.SINGLE_SUCCESS
            }
            // if (context.source.player?.uuid != player.uuid && ) {}
            val pokemon = pokemonProperties.create()
            val party = Pokemod.storage.getParty(player)
            party.add(pokemon)
            context.source.sendFeedback(commandLang("givepokemon.give", pokemon.species.translatedName, player.name), true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Command.SINGLE_SUCCESS
    }
}