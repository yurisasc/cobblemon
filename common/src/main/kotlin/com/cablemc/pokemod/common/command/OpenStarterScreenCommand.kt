/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.command

import com.cablemc.pokemod.common.Pokemod
import com.cablemc.pokemod.common.PokemodNetwork.sendPacket
import com.cablemc.pokemod.common.api.permission.PermissionLevel
import com.cablemc.pokemod.common.api.permission.PokemodPermissions
import com.cablemc.pokemod.common.api.text.red
import com.cablemc.pokemod.common.net.messages.client.starter.OpenStarterUIPacket
import com.cablemc.pokemod.common.util.lang
import com.cablemc.pokemod.common.util.permission
import com.cablemc.pokemod.common.util.permissionLevel
import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource

object OpenStarterScreenCommand {

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            literal("openstarterscreen")
                .permission(PokemodPermissions.OPEN_STARTER_SCREEN)
                .permissionLevel(PermissionLevel.MULTIPLAYER_MANAGEMENT)
                .then(
                    argument("player", EntityArgumentType.player())
                        .executes { execute(it,) }
                )
        )
    }

    private fun execute(context: CommandContext<ServerCommandSource>) : Int {
        val player = EntityArgumentType.getPlayer(context, "player")
        val playerData = Pokemod.playerData.get(player)
        if (playerData.starterSelected) {
            context.source.sendFeedback(lang("ui.starter.hasalreadychosen", player.name).red(), true)
            return 0
        }
        if (playerData.starterLocked) {
            playerData.starterLocked = false
            playerData.sendToPlayer(player)
        }
        playerData.starterPrompted = true
        Pokemod.playerData.saveSingle(playerData)
        player.sendPacket(OpenStarterUIPacket(Pokemod.starterHandler.getStarterList(player)))
        return SINGLE_SUCCESS
    }

}