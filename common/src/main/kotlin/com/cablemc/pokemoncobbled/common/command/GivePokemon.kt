package com.cablemc.pokemoncobbled.common.command

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.moves.Moves
import com.cablemc.pokemoncobbled.common.command.argument.PokemonArgumentType
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.commandLang
import com.cablemc.pokemoncobbled.common.util.player
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.server.level.ServerPlayer

object GivePokemon {

    fun register(dispatcher : CommandDispatcher<CommandSourceStack>) {
        val command = Commands.literal("givepokemon")
            .requires { it.hasPermission(4) }
            .then(
                Commands.argument("pokemon", PokemonArgumentType.pokemon())
                    .requires { it != it.server}
                    .executes { execute(it, it.source.playerOrException) }
            )
            .then(
                Commands.argument("player", EntityArgument.player())
                    .then(Commands.argument("pokemon", PokemonArgumentType.pokemon())
                        .executes { execute(it, it.player()) }
                    )
            )

        dispatcher.register(command)
    }

    private fun execute(context: CommandContext<CommandSourceStack>, player: ServerPlayer) : Int {
        try {
            val pkm = PokemonArgumentType.getPokemon(context, "pokemon")
            val pokemon = Pokemon().apply { species = pkm }
            val party = PokemonCobbled.storage.getParty(player)
            pokemon.moveSet.setMove(0, Moves.getByName("tackle")!!.create())
            pokemon.moveSet.setMove(1, Moves.getByName("aerialace")!!.create())
            pokemon.moveSet.setMove(2, Moves.getByName("airslash")!!.create())
            pokemon.moveSet.setMove(3, Moves.getByName("aurasphere")!!.create())
            party.add(pokemon)
            context.source.sendSuccess(commandLang("givepokemon.give", pkm.translatedName, player.name), true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Command.SINGLE_SUCCESS
    }
}