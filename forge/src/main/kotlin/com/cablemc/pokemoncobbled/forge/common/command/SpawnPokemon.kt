package com.cablemc.pokemoncobbled.forge.common.command

import com.cablemc.pokemoncobbled.forge.common.command.argument.PokemonArgumentType
import com.cablemc.pokemoncobbled.forge.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.forge.common.pokemon.Pokemon
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer

object SpawnPokemon {

    fun register(dispatcher : CommandDispatcher<CommandSourceStack>) {
        val command = Commands.literal("spawnpokemon")
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