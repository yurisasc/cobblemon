package com.cablemc.pokemoncobbled.common.command

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.command.argument.PokemonArgumentType
import com.cablemc.pokemoncobbled.common.command.provider.PokemonNameSuggestions
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player

object PokeSpawn {

    fun register(dispatcher : CommandDispatcher<CommandSourceStack>) {
        val command = Commands.literal("pokespawn")
            .requires { it.hasPermission(4) }
            .then(
                Commands.argument("pokemon", PokemonArgumentType.pokemon())
                    .executes { execute(it) })
        dispatcher.register(command)
    }

    private fun execute(context: CommandContext<CommandSourceStack>) : Int {
        val entity = context.source.entity
        if (entity is ServerPlayer && !entity.level.isClientSide) {
            val pkm = PokemonArgumentType.getPokemon(context, "pokemon")
            val player = context.source.playerOrException
            val pokemonEntity = PokemonEntity(entity.level as ServerLevel)
            pokemonEntity.let {
                it.pokemon = Pokemon().apply {
                    species = pkm
                    form = species.forms.first()
                }
                it.dexNumber.set(it.pokemon.species.nationalPokedexNumber)
            }
            entity.level.addFreshEntity(pokemonEntity)
            pokemonEntity.setPos(entity.position())
        }
        return Command.SINGLE_SUCCESS
    }

}