package com.cablemc.pokemoncobbled.common.command

import com.cablemc.pokemoncobbled.common.api.text.text
import com.cablemc.pokemoncobbled.common.util.party
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity

object TakePokemon {
    fun register(dispatcher : CommandDispatcher<ServerCommandSource>) {
        val command = CommandManager.literal("takepokemon")
            .requires { it.hasPermissionLevel(4) }
            .then(
                CommandManager.argument("player", EntityArgumentType.player())
                    .then(
                        CommandManager.argument("slot", IntegerArgumentType.integer(1, 99))
                            .executes { execute(it) }
                    )
            )

        dispatcher.register(command)
    }

    private fun execute(context: CommandContext<ServerCommandSource>) : Int {
        try {
            val target = EntityArgumentType.getPlayer(context, "player")
            val slot = IntegerArgumentType.getInteger(context, "slot")
            val party = target.party()

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

            party.remove(pokemon)
            if (context.source.entity != target) {
                if (context.source.entity is ServerPlayerEntity) {
                    val toParty = context.source.player.party()
                    toParty.add(pokemon)
                    context.source.sendFeedback("You took ${pokemon.species.name}".text(), true)
                    return Command.SINGLE_SUCCESS
                }
            }

            context.source.sendFeedback("${pokemon.species.name} was removed.".text(), true)
            return Command.SINGLE_SUCCESS
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Command.SINGLE_SUCCESS
    }
}