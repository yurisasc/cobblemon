/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.command

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonNetwork.sendPacket
import com.cobblemon.mod.common.api.permission.CobblemonPermissions
import com.cobblemon.mod.common.api.storage.player.PlayerInstancedDataStoreType
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.command.argument.FormArgumentType
import com.cobblemon.mod.common.command.argument.SpeciesArgumentType
import com.cobblemon.mod.common.config.pokedex.PokedexConfig.form
import com.cobblemon.mod.common.events.PokedexHandler
import com.cobblemon.mod.common.net.messages.client.SetClientPlayerDataPacket
import com.cobblemon.mod.common.pokemon.FormData
import com.cobblemon.mod.common.pokemon.Species
import com.cobblemon.mod.common.util.permission
import com.cobblemon.mod.common.util.player
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.EntitySelector
import net.minecraft.command.argument.ArgumentTypes
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

object PokedexCommand {

    private const val NAME = "pokedex"
    private const val GRANT_NAME = "grant"
    private const val REVOKE_NAME = "revoke"
    fun register(dispatcher : CommandDispatcher<ServerCommandSource>) {
        val commandArgumentBuilder = CommandManager.literal(NAME)
        val grantCommandBuilder = CommandManager.literal(GRANT_NAME).then(
            CommandManager.argument("player", EntityArgumentType.player())
                .then(CommandManager.literal("all").executes(::executeGrantAll))
                .then(CommandManager.literal("only").then(
                    CommandManager.argument("species", SpeciesArgumentType.species()).then(
                        CommandManager.argument("form", FormArgumentType.form()).executes(::executeGrantOnly)
                    )
                ))
        )
        val revokeCommandBuilder = CommandManager.literal(REVOKE_NAME).then(
            CommandManager.argument("player", EntityArgumentType.player())
                .then(CommandManager.literal("all").executes(::executeRemoveAll))
                .then(CommandManager.literal("only").then(
                    CommandManager.argument("species", SpeciesArgumentType.species()).then(
                        CommandManager.argument("form", FormArgumentType.form()).executes(::executeRemoveOnly)
                    )
                ))
        )
        commandArgumentBuilder
            .then(grantCommandBuilder)
            .then(revokeCommandBuilder)
            .permission(CobblemonPermissions.POKEDEX)


        dispatcher.register(commandArgumentBuilder)
    }

    private fun executeGrantOnly(context: CommandContext<ServerCommandSource>): Int {
        val players = context.getArgument("player", EntitySelector::class.java).getPlayers(context.source)
        val species = context.getArgument("species", Species::class.java)
        val form = context.getArgument("form", FormData::class.java)
        players.forEach {
            val dex = Cobblemon.playerDataManager.getPokedexData(it)
            dex.grantedWithCommand(species, form)
            it.sendPacket(SetClientPlayerDataPacket(PlayerInstancedDataStoreType.POKEDEX, dex.toClientData()))
        }
        val selectorStr = if (players.size == 1) players.first().name.string else "${players.size} players"
        context.source.sendMessage(
            Text.of("Granted ${species.name}-${form.formOnlyShowdownId()} to $selectorStr")
        )
        return Command.SINGLE_SUCCESS
    }

    private fun executeRemoveOnly(context: CommandContext<ServerCommandSource>): Int {
        val players = context.getArgument("player", EntitySelector::class.java).getPlayers(context.source)
        val species = context.getArgument("species", Species::class.java)
        val form = context.getArgument("form", FormData::class.java)
        players.forEach {
            val dex = Cobblemon.playerDataManager.getPokedexData(it)
            dex.removedByCommand(species, form)
            it.sendPacket(SetClientPlayerDataPacket(PlayerInstancedDataStoreType.POKEDEX, dex.toClientData()))
        }
        val selectorStr = if (players.size == 1) players.first().name.string else "${players.size} players"
        context.source.sendMessage(
            Text.of("Removed ${species.name}-${form.formOnlyShowdownId()} from $selectorStr")
        )
        return Command.SINGLE_SUCCESS
    }
    private fun executeGrantAll(context: CommandContext<ServerCommandSource>): Int {
        val players = context.getArgument("player", EntitySelector::class.java).getPlayers(context.source)
        players.forEach {
            val dex = Cobblemon.playerDataManager.getPokedexData(it)
            dex.grantedWithCommand(null, null)
            it.sendPacket(SetClientPlayerDataPacket(PlayerInstancedDataStoreType.POKEDEX, dex.toClientData()))
        }
        val selectorStr = if (players.size == 1) players.first().name.string else "${players.size} players"
        context.source.sendMessage(
            Text.of("Filled dex of $selectorStr")
        )
        return Command.SINGLE_SUCCESS
    }

    private fun executeRemoveAll(context: CommandContext<ServerCommandSource>): Int {
        val players = context.getArgument("player", EntitySelector::class.java).getPlayers(context.source)
        players.forEach {
            val dex = Cobblemon.playerDataManager.getPokedexData(it)
            dex.removedByCommand(null, null)
            it.sendPacket(SetClientPlayerDataPacket(PlayerInstancedDataStoreType.POKEDEX, dex.toClientData()))
        }
        val selectorStr = if (players.size == 1) players.first().name.string else "${players.size} players"
        context.source.sendMessage(
            Text.of("Cleared dex of $selectorStr")
        )
        return Command.SINGLE_SUCCESS
    }
}