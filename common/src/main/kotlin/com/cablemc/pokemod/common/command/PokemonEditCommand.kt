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
import com.cablemc.pokemod.common.command.argument.PartySlotArgumentType
import com.cablemc.pokemod.common.command.argument.PokemonPropertiesArgumentType
import com.cablemc.pokemod.common.util.commandLang
import com.cablemc.pokemod.common.util.permission
import com.cablemc.pokemod.common.util.permissionLevel
import com.cablemc.pokemod.common.util.player
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource

object PokemonEditCommand {

    private const val NAME = "pokemonedit"
    private const val PLAYER = "player"
    private const val SLOT = "slot"
    private const val PROPERTIES = "properties"

    fun register(dispatcher : CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(literal(NAME)
            .permission(PokemodPermissions.POKEMON_EDIT_SELF)
            .permissionLevel(PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS)
            .then(argument(PLAYER, EntityArgumentType.player())
                .permission(PokemodPermissions.POKEMON_EDIT_OTHER)
                .permissionLevel(PermissionLevel.MULTIPLAYER_MANAGEMENT)
                .then(argument(SLOT, PartySlotArgumentType.partySlot())
                    .then(argument(PROPERTIES, PokemonPropertiesArgumentType.properties())
                        .executes(this::execute)
                    ))
            )
        )
    }

    private fun execute(context: CommandContext<ServerCommandSource>): Int {
        val player = context.player()
        val pokemon = PartySlotArgumentType.getPokemon(context, SLOT)
        // They may change the species, think it makes sense to say the existing thing was edited, or maybe it doesn't & I'm a derp
        val oldName = pokemon.species.translatedName
        val properties = PokemonPropertiesArgumentType.getPokemonProperties(context, PROPERTIES)
        properties.apply(pokemon)
        context.source.sendFeedback(commandLang(NAME, oldName, player.name), true)
        return Command.SINGLE_SUCCESS
    }

}