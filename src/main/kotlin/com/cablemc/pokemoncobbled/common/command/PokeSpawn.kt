package com.cablemc.pokemoncobbled.common.command

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.command.provider.PokemonNameSuggestions
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.server.level.ServerLevel

object PokeSpawn {

    fun register(dispatcher : CommandDispatcher<CommandSourceStack>) {
        val command = Commands.literal("pokespawn")
            .requires { it.hasPermission(4) }
            .then(Commands.argument("species", StringArgumentType.word())
                .suggests(PokemonNameSuggestions())
                .executes { execute(it) })
        dispatcher.register(command)
    }

    private fun execute(context: CommandContext<CommandSourceStack>) : Int {
        val speciesName = StringArgumentType.getString(context, "species")
        val speciesArg = PokemonSpecies.getByName(speciesName) ?: PokemonSpecies.species.random()
        val player = context.source.playerOrException
        val pokemonEntity = PokemonEntity(player.level as ServerLevel)
        pokemonEntity.let {
            it.pokemon = Pokemon().apply { species = speciesArg }
            it.dexNumber.set(it.pokemon.species.nationalPokedexNumber)
        }
        player.level.addFreshEntity(pokemonEntity)
        pokemonEntity.setPos(player.position())
        return 1
    }

}