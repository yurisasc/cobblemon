package com.cablemc.pokemoncobbled.common.command

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.command.argument.PartySlotArgumentType
import com.cablemc.pokemoncobbled.common.util.commandLang
import com.cablemc.pokemoncobbled.common.util.player
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.server.level.ServerPlayer

object PokemonEditCommand {

    private const val NAME = "pokemonedit"
    private const val PLAYER = "player"
    private const val SLOT = "slot"
    private const val PROPERTIES = "properties"

    fun register(dispatcher : CommandDispatcher<CommandSourceStack>) {
        val command = Commands.literal(NAME)
            .requires { it.hasPermission(4)  }
            .then(Commands.argument(PLAYER, EntityArgument.player())
                .then(createCommonArguments { it.player() })
            )
            .then(createCommonArguments { it.source.playerOrException })
        dispatcher.register(command)
    }

    private fun createCommonArguments(playerResolver: (CommandContext<CommandSourceStack>) -> ServerPlayer): ArgumentBuilder<CommandSourceStack, *> {
        return Commands.argument(SLOT, PartySlotArgumentType.partySlot())
            .then(Commands.argument(PROPERTIES, StringArgumentType.greedyString())
                .executes { execute(it, playerResolver.invoke(it)) }
            )
    }

    private fun execute(context: CommandContext<CommandSourceStack>, player: ServerPlayer) : Int {
        val pokemon = PartySlotArgumentType.getPokemon(context, SLOT)
        // They may change the species, think it makes sense to say the existing thing was edited, or maybe it doesn't & I'm a derp
        val oldName = pokemon.species.translatedName
        val properties = PokemonProperties.parse(StringArgumentType.getString(context, PROPERTIES))
        properties.apply(pokemon)
        context.source.sendSuccess(commandLang(NAME, oldName, player.name), true)
        return Command.SINGLE_SUCCESS
    }

}