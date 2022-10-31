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
import com.cablemc.pokemod.common.pokemon.Pokemon
import com.cablemc.pokemod.common.util.commandLang
import com.cablemc.pokemod.common.util.permission
import com.cablemc.pokemod.common.util.permissionLevel
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity

object FriendshipCommand {

    fun register(dispatcher : CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("friendship")
            .permission(PokemodPermissions.FRIENDSHIP)
            .permissionLevel(PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS)
            .then(
                CommandManager.argument("slot", PartySlotArgumentType.partySlot())
                    .executes { execute(it.source, it.source.playerOrThrow, PartySlotArgumentType.getPokemon(it, "slot")) }
            ))
    }

    private fun execute(source: ServerCommandSource, target: ServerPlayerEntity, pokemon: Pokemon) : Int {
        if (!target.world.isClient) {
            val feedback = commandLang("friendship", pokemon.displayName, pokemon.friendship)
            source.sendFeedback(feedback, true)
        }
        return Command.SINGLE_SUCCESS
    }

}