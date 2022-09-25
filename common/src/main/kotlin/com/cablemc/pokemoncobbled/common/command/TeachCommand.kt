/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.command

import com.cablemc.pokemoncobbled.common.api.moves.BenchedMove
import com.cablemc.pokemoncobbled.common.api.permission.CobbledPermissions
import com.cablemc.pokemoncobbled.common.api.permission.PermissionLevel
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.command.argument.MoveArgumentType
import com.cablemc.pokemoncobbled.common.command.argument.PartySlotArgumentType
import com.cablemc.pokemoncobbled.common.util.commandLang
import com.cablemc.pokemoncobbled.common.util.permission
import com.cablemc.pokemoncobbled.common.util.permissionLevel
import com.cablemc.pokemoncobbled.common.util.player
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity

object TeachCommand {

    private const val NAME = "teach"
    private const val PLAYER = "player"
    private const val SLOT = "slot"
    private const val MOVE = "move"

    fun register(dispatcher : CommandDispatcher<ServerCommandSource>) {
        val command = CommandManager.literal(NAME)
            .permission(CobbledPermissions.TEACH)
            .permissionLevel(PermissionLevel.MULTIPLAYER_MANAGEMENT)
            .then(CommandManager.argument(PLAYER, EntityArgumentType.player())
                .then(CommandManager.argument(SLOT, PartySlotArgumentType.partySlot())
                    .then(CommandManager.argument(MOVE, MoveArgumentType.move())
                        .executes { execute(it, it.player()) }
                    ))
            )
        dispatcher.register(command)
    }

    private fun execute(context: CommandContext<ServerCommandSource>, player: ServerPlayerEntity) : Int {
        val pokemon = PartySlotArgumentType.getPokemon(context, SLOT)
        val move = MoveArgumentType.getMove(context, MOVE)
        pokemon.benchedMoves.add(BenchedMove(move, 0))
        context.source.sendFeedback(commandLang(NAME, pokemon.species.translatedName, player.name, move.displayName), true)
        return Command.SINGLE_SUCCESS
    }

}