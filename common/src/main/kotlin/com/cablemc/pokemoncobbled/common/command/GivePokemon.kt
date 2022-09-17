/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.command

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.command.argument.PokemonPropertiesArgumentType
import com.cablemc.pokemoncobbled.common.util.commandLang
import com.cablemc.pokemoncobbled.common.util.player
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
            .requires { it.hasPermissionLevel(4) }
            .then(
                CommandManager.argument("pokemon", PokemonPropertiesArgumentType.properties())
                    .requires { it.player != null }
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
            val pokemon = pokemonProperties.create()
            val party = PokemonCobbled.storage.getParty(player)
            party.add(pokemon)
            context.source.sendFeedback(commandLang("givepokemon.give", pokemon.species.translatedName, player.name), true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Command.SINGLE_SUCCESS
    }
}