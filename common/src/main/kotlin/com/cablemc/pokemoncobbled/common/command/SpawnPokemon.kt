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
import com.cablemc.pokemoncobbled.common.command.argument.PokemonPropertiesArgumentType
import com.cablemc.pokemoncobbled.common.util.permission
import com.cablemc.pokemoncobbled.common.util.permissionLevel
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity

object SpawnPokemon {

    fun register(dispatcher : CommandDispatcher<ServerCommandSource>) {
        val command = dispatcher.register(literal("spawnpokemon")
            .permission(CobbledPermissions.SPAWN_POKEMON)
            .permissionLevel(PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS)
            .then(
                CommandManager.argument("pokemon", PokemonPropertiesArgumentType.properties())
                    .executes { execute(it) }
            ))
        dispatcher.register(literal("pokespawn").redirect(command))
    }

    private fun execute(context: CommandContext<ServerCommandSource>) : Int {
        val entity = context.source.entity
        if (entity is ServerPlayerEntity && !entity.world.isClient) {
            val pkm = PokemonPropertiesArgumentType.getPokemonProperties(context, "pokemon")
            val pokemonEntity = pkm.createEntity(entity.world)
            entity.world.spawnEntity(pokemonEntity)
            pokemonEntity.setPosition(entity.pos)
        }
        return Command.SINGLE_SUCCESS
    }

}