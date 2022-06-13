package com.cablemc.pokemoncobbled.common.command

import com.cablemc.pokemoncobbled.common.command.argument.PokemonPropertiesArgumentType
import com.cablemc.pokemoncobbled.common.util.party
import com.cablemc.pokemoncobbled.common.util.player
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity

object HealPokemonCommand {

    fun register(dispatcher : CommandDispatcher<ServerCommandSource>) {
        val command = CommandManager.literal("healpokemon")
            .requires { it.hasPermissionLevel(4) }
            .then(
                CommandManager.argument("player", EntityArgumentType.player())
                    .executes { execute(it) }
            )
        dispatcher.register(command)
    }

    private fun execute(context: CommandContext<ServerCommandSource>) : Int {
        val entity = context.source.entity
        val player = context.player("player") ?: (if (entity is ServerPlayerEntity) entity else return 0)
        if (!player.world.isClient) {
            val party = player.party()
            party.heal()
            // TODO send message for healing
        }
        return Command.SINGLE_SUCCESS
    }

}