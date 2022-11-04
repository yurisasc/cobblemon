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
import com.cablemc.pokemod.common.api.text.red
import com.cablemc.pokemod.common.command.argument.PokemonPropertiesArgumentType
import com.cablemc.pokemod.common.util.commandLang
import com.cablemc.pokemod.common.util.permission
import com.cablemc.pokemod.common.util.permissionLevel
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
            .permission(PokemodPermissions.SPAWN_POKEMON)
            .permissionLevel(PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS)
            .then(
                CommandManager.argument("pokemon", PokemonPropertiesArgumentType.properties())
                    .executes(this::execute)
            ))
        dispatcher.register(literal("pokespawn").redirect(command))
    }

    private fun execute(context: CommandContext<ServerCommandSource>) : Int {
        val entity = context.source.entity
        if (entity is ServerPlayerEntity && !entity.world.isClient) {
            val pkm = PokemonPropertiesArgumentType.getPokemonProperties(context, "pokemon")
            if (pkm.species == null) {
                entity.sendMessage(commandLang("spawnpokemon.nospecies").red())
                return Command.SINGLE_SUCCESS
            }
            val pokemonEntity = pkm.createEntity(entity.world)
            entity.world.spawnEntity(pokemonEntity)
            pokemonEntity.setPosition(entity.pos)
        }
        return Command.SINGLE_SUCCESS
    }

}