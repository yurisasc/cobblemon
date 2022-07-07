package com.cablemc.pokemoncobbled.common.command

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.pokemon.status.Statuses
import com.cablemc.pokemoncobbled.common.command.argument.PokemonPropertiesArgumentType
import com.cablemc.pokemoncobbled.common.util.commandLang
import com.cablemc.pokemoncobbled.common.util.player
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.command.CommandManager.literal

object GivePokemon {

    fun register(dispatcher : CommandDispatcher<ServerCommandSource>) {
        val command = dispatcher.register(literal("givepokemon")
            .requires { it.hasPermissionLevel(4) }
            .then(
                CommandManager.argument("pokemon", PokemonPropertiesArgumentType.properties())
                    .requires { it != it.server}
                    .executes { execute(it, it.source.player) }
            )
            .then(
                CommandManager.argument("player", EntityArgumentType.player())
                    .then(CommandManager.argument("pokemon", PokemonPropertiesArgumentType.properties())
                        .executes { execute(it, it.player()) }
                    )
            ))
        dispatcher.register(literal("pokegive").redirect(command))
    }

    private fun execute(context: CommandContext<ServerCommandSource>, player: ServerPlayerEntity) : Int {
        try {
            val pokemonProperties = PokemonPropertiesArgumentType.getPokemonProperties(context, "pokemon")
            val pokemon = pokemonProperties.create()
            pokemon.moveSet.get(1)?.currentPp = 10
            val party = PokemonCobbled.storage.getParty(player)
            party.add(pokemon)
            context.source.sendFeedback(commandLang("givepokemon.give", pokemon.species.translatedName, player.name), true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Command.SINGLE_SUCCESS
    }
}