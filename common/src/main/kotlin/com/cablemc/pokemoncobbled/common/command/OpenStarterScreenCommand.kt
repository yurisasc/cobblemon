package com.cablemc.pokemoncobbled.common.command

import com.cablemc.pokemoncobbled.common.CobbledNetwork.sendPacket
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.text.red
import com.cablemc.pokemoncobbled.common.net.messages.client.starter.OpenStarterUIPacket
import com.cablemc.pokemoncobbled.common.util.lang
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
                .requires { it.hasPermissionLevel(4) }
                .then(
                    argument("player", EntityArgumentType.player())
                        .executes { execute(it,) }
                )
        )
    }

    private fun execute(context: CommandContext<ServerCommandSource>) : Int {
        val player = EntityArgumentType.getPlayer(context, "player")
        val playerData = PokemonCobbled.playerData.get(player)
        if (playerData.starterSelected) {
            context.source.sendFeedback(lang("ui.starter.hasalreadychosen", player.name).red(), true)
            return 0
        }
        if (playerData.starterLocked) {
            playerData.starterLocked = false
            playerData.sendToPlayer(player)
        }
        playerData.starterPrompted = true
        PokemonCobbled.playerData.saveSingle(playerData)
        player.sendPacket(OpenStarterUIPacket(PokemonCobbled.starterHandler.getStarterList(player)))
        return SINGLE_SUCCESS
    }

}