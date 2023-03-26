/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.command

import com.cobblemon.mod.common.api.permission.CobblemonPermissions
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.command.argument.PartySlotArgumentType
import com.cobblemon.mod.common.net.messages.client.storage.RemoveClientPokemonPacket
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.party
import com.cobblemon.mod.common.util.pc
import com.cobblemon.mod.common.util.permission
import com.cobblemon.mod.common.util.player
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

/**
 * Send Pokemon to the PC.
 *
 * Two similar commands with the syntax:
 *
 * `/pokebox <player> <slot> [box]` Sends a single Pokemon to the PC, allowing for a specific box to be selected.
 *  If a box is selected and is full, no action will take place.
 *
 * `/pokeboxall <player> [box]` Sends all party pokemon to the PC, allowing for a specific box to be selected.
 *  If a box is selected and would not beable to house all of the party Pokemon, no action will take place.
 */
object PokeboxCommand {
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(literal("pokebox")
            .permission(CobblemonPermissions.POKEBOX)
            .then(CommandManager.argument("player", EntityArgumentType.player())
                .then(CommandManager.argument("slot", PartySlotArgumentType.partySlot())
                    .then(CommandManager.argument("box", IntegerArgumentType.integer(1))
                        .executes { context ->
                            val player = context.player()
                            val pokemon = PartySlotArgumentType.getPokemonOf(context, "slot", player)
                            val box = IntegerArgumentType.getInteger(context, "box")
                            execute(context, player, listOf(pokemon), box)
                        })
                    .executes { context ->
                        val player = context.player()
                        val pokemon = PartySlotArgumentType.getPokemonOf(context, "slot", player)
                        execute(context, player, listOf(pokemon))
                    })
            )
        )

        dispatcher.register(literal("pokeboxall")
            .permission(CobblemonPermissions.POKEBOX)
            .then(CommandManager.argument("player", EntityArgumentType.player())
                .then(CommandManager.argument("box", IntegerArgumentType.integer(1))
                    .executes { context ->
                        val player = context.player()
                        val box = IntegerArgumentType.getInteger(context, "box")
                        execute(context, player, player.party().toList(), box)
                    })
                .executes { context ->
                    val player = context.player()
                    execute(context, player, player.party().toList())
                }))
    }
}

private fun execute(
    context: CommandContext<ServerCommandSource>,
    player: ServerPlayerEntity,
    pokemons: Collection<Pokemon>,
    box: Int? = null,
): Int {
    val playerPc = player.pc()
    val playerParty = player.party()

    // If specifying a box, first check that the box exists and can sufficiently hold all the pokemon to be moved.
    if (box != null) {
        if (playerPc.boxes.size < box) {
            throw SimpleCommandExceptionType(Text.literal("That player doesn't have a box $box").red()).create()
        }

        val pcBox = playerPc.boxes.get(box - 1)

        if (pcBox.unoccupiedSlots < pokemons.size) {
            throw SimpleCommandExceptionType(Text.literal("Unable to make space in the PC Box.").red()).create()
        }
    }

    pokemons.forEach { pokemon ->
        // If PCStore and PCBox both implemented PokemonStore we could make this code a lot cleaner via the same interface
        val pcPosition = if (box == null) {
            playerPc.getFirstAvailablePosition()
                ?: throw SimpleCommandExceptionType(Text.literal("Unable to find an available spot for your pokemon").red()).create()
        } else {
            val pcBox = playerPc.boxes.get(box - 1)
            pcBox.getFirstAvailablePosition()
                ?: throw SimpleCommandExceptionType(Text.literal("Unable to find an available spot for your pokemon").red()).create()
        }

        playerParty.remove(pokemon)
        playerPc[pcPosition] = pokemon

        // Let the client(s) know about the change to party
        playerParty.sendPacketToObservers(
            RemoveClientPokemonPacket(player.party(), pokemon.uuid)
        )
    }

    // Let the call know how many entities were moved
    return pokemons.size
}
