package com.cablemc.pokemoncobbled.common.command

import com.cablemc.pokemoncobbled.common.api.text.text
import com.cablemc.pokemoncobbled.common.util.party
import com.cablemc.pokemoncobbled.common.util.player
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.server.level.ServerPlayerEntity

object LevelUp {

    fun register(dispatcher : CommandDispatcher<CommandSourceStack>) {
        val command = Commands.literal("levelup")
            .requires { it.hasPermission(4) }
            .then(
                Commands.argument("player", EntityArgument.player())
                    .then(
                        Commands.argument("slot", IntegerArgumentType.integer(1, 99))
                            .executes { execute(it, it.player()) }
                    )
            )
            .then(
                Commands.argument("slot", IntegerArgumentType.integer(1, 99))
                    .requires { it.entity is ServerPlayerEntity }
                    .executes { execute(it, it.source.playerOrException) }
            )

        dispatcher.register(command)
    }

    private fun execute(context: CommandContext<CommandSourceStack>, player: ServerPlayerEntity) : Int {
        val slot = IntegerArgumentType.getInteger(context, "slot")
        val party = player.party()
        if (slot > party.size()) {
            // todo translate
            context.source.sendFailure("Your party only has ${party.size()} slots.".text())
            return 0
        }

        val pokemon = party.get(slot - 1)
        if (pokemon == null) {
            context.source.sendFailure("There is no Pokémon in slot $slot".text())
            return 0
        }

        pokemon.addExperienceWithPlayer(player, pokemon.getExperienceToNextLevel())
        return Command.SINGLE_SUCCESS
    }
}