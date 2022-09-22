/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.command

import com.cablemc.pokemoncobbled.common.PokemonCobbled.LOGGER
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.world.ServerWorld

object SpawnAllPokemon {
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("spawnallpokemon")
                .requires { it.hasPermissionLevel(4) }
                .requires { it.player != null }
                .then(
                    CommandManager.argument("min", IntegerArgumentType.integer(1))
                        .then(
                            CommandManager.argument("max", IntegerArgumentType.integer(1))
                                .executes {
                                    execute(it, IntegerArgumentType.getInteger(it, "min")..IntegerArgumentType.getInteger(it, "max"))
                                }
                        )
                        .executes { execute(it, IntegerArgumentType.getInteger(it, "min")..Int.MAX_VALUE) }
                )
                .executes { execute(it, 1..Int.MAX_VALUE) }
        )
    }

    private fun execute(context: CommandContext<ServerCommandSource>, range: IntRange) : Int {
        val player = context.source.playerOrThrow

        for (species in PokemonSpecies.species) {
            if (species.nationalPokedexNumber in range) {
                LOGGER.debug(species.name)
                species.create().sendOut(player.world as ServerWorld, player.pos)
            }
        }

        return Command.SINGLE_SUCCESS
    }
}