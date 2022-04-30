package com.cablemc.pokemoncobbled.common.command

import com.cablemc.pokemoncobbled.common.api.text.text
import com.cablemc.pokemoncobbled.common.util.party
import com.cablemc.pokemoncobbled.common.util.player
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity

object LevelUp {

    fun register(dispatcher : CommandDispatcher<ServerCommandSource>) {
        val command = CommandManager.literal("levelup")
            .requires { it.hasPermissionLevel(4) }
            .then(
                CommandManager.argument("player", EntityArgumentType.player())
                    .then(
                        CommandManager.argument("slot", IntegerArgumentType.integer(1, 99))
                            .executes { execute(it, it.player()) }
                    )
            )
            .then(
                CommandManager.argument("slot", IntegerArgumentType.integer(1, 99))
                    .requires { it.entity is ServerPlayerEntity }
                    .executes { execute(it, it.source.player) }
            )

        dispatcher.register(command)
    }

    private fun execute(context: CommandContext<ServerCommandSource>, player: ServerPlayerEntity) : Int {
        val slot = IntegerArgumentType.getInteger(context, "slot")
        val party = player.party()
        if (slot > party.size()) {
            // todo translate
            context.source.sendError("Your party only has ${party.size()} slots.".text())
            return 0
        }

        val pokemon = party.get(slot - 1)
        if (pokemon == null) {
            context.source.sendError("There is no Pokémon in slot $slot".text())
            return 0
        }

        pokemon.addExperienceWithPlayer(player, pokemon.getExperienceToNextLevel())
        return Command.SINGLE_SUCCESS
    }
}