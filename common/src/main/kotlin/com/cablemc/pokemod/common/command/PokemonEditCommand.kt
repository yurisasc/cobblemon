/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.command

import com.cablemc.pokemod.common.api.permission.PermissionLevel
import com.cablemc.pokemod.common.api.permission.PokemodPermissions
import com.cablemc.pokemod.common.api.pokemon.PokemonProperties
import com.cablemc.pokemod.common.command.argument.PartySlotArgumentType
import com.cablemc.pokemod.common.util.commandLang
import com.cablemc.pokemod.common.util.permission
import com.cablemc.pokemod.common.util.permissionLevel
import com.cablemc.pokemod.common.util.player
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity

object PokemonEditCommand {

    private const val NAME = "pokemonedit"
    private const val PLAYER = "player"
    private const val SLOT = "slot"
    private const val PROPERTIES = "properties"

    fun register(dispatcher : CommandDispatcher<ServerCommandSource>) {
        val command = CommandManager.literal(NAME)
            .permission(PokemodPermissions.POKEMON_EDIT_SELF)
            .permissionLevel(PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS)
            .then(CommandManager.argument(PLAYER, EntityArgumentType.player())
                .permission(PokemodPermissions.POKEMON_EDIT_OTHER)
                .permissionLevel(PermissionLevel.MULTIPLAYER_MANAGEMENT)
                .then(createCommonArguments { it.player() })
            )
            .then(createCommonArguments { it.source.playerOrThrow })
        dispatcher.register(command)
    }

    private fun createCommonArguments(playerResolver: (CommandContext<ServerCommandSource>) -> ServerPlayerEntity): ArgumentBuilder<ServerCommandSource, *> {
        return CommandManager.argument(SLOT, PartySlotArgumentType.partySlot())
            .then(CommandManager.argument(PROPERTIES, StringArgumentType.greedyString())
                .executes { execute(it, playerResolver.invoke(it)) }
            )
    }

    private fun execute(context: CommandContext<ServerCommandSource>, player: ServerPlayerEntity) : Int {
        val pokemon = PartySlotArgumentType.getPokemon(context, SLOT)
        // They may change the species, think it makes sense to say the existing thing was edited, or maybe it doesn't & I'm a derp
        val oldName = pokemon.species.translatedName
        val properties = PokemonProperties.parse(StringArgumentType.getString(context, PROPERTIES))
        properties.apply(pokemon)
        context.source.sendFeedback(commandLang(NAME, oldName, player.name), true)
        return Command.SINGLE_SUCCESS
    }

}