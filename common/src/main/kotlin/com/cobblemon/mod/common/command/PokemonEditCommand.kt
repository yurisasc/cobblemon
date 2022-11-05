/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.command

import com.cobblemon.mod.common.api.permission.CobblemonPermissions
import com.cobblemon.mod.common.api.permission.PermissionLevel
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.command.argument.PartySlotArgumentType
import com.cobblemon.mod.common.command.argument.PokemonPropertiesArgumentType
import com.cobblemon.mod.common.util.commandLang
import com.cobblemon.mod.common.util.permission
import com.cobblemon.mod.common.util.permissionLevel
import com.cobblemon.mod.common.util.player
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
            .permission(CobblemonPermissions.POKEMON_EDIT_SELF)
            .permissionLevel(PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS)
            .then(argument(PLAYER, EntityArgumentType.player())
                .permission(CobblemonPermissions.POKEMON_EDIT_OTHER)
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