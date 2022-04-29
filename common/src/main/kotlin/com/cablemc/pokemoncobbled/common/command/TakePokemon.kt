package com.cablemc.pokemoncobbled.common.command

import com.cablemc.pokemoncobbled.common.api.text.text
import com.cablemc.pokemoncobbled.common.util.party
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.server.level.ServerPlayerEntity

object TakePokemon {
    fun register(dispatcher : CommandDispatcher<CommandSourceStack>) {
        val command = Commands.literal("takepokemon")
            .requires { it.hasPermission(4) }
            .then(
                Commands.argument("player", EntityArgument.player())
                    .then(
                        Commands.argument("slot", IntegerArgumentType.integer(1, 99))
                            .executes { execute(it) }
                    )
            )

        dispatcher.register(command)
    }

    private fun execute(context: CommandContext<CommandSourceStack>) : Int {
        try {
            val target = EntityArgument.getPlayer(context, "player")
            val slot = IntegerArgumentType.getInteger(context, "slot")
            val party = target.party()

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

            party.remove(pokemon)
            if (context.source.entity != target) {
                if (context.source.entity is ServerPlayerEntity) {
                    val toParty = context.source.playerOrException.party()
                    toParty.add(pokemon)
                    context.source.sendSuccess("You took ${pokemon.species.name}".text(), true)
                    return Command.SINGLE_SUCCESS
                }
            }

            context.source.sendSuccess("${pokemon.species.name} was removed.".text(), true)
            return Command.SINGLE_SUCCESS
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Command.SINGLE_SUCCESS
    }
}