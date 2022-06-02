package com.cablemc.pokemoncobbled.common.command

import com.cablemc.pokemoncobbled.common.command.argument.PokemonArgumentType
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld

object SpawnPokemon {

    fun register(dispatcher : CommandDispatcher<ServerCommandSource>) {
        val command = CommandManager.literal("spawnpokemon")
            .requires { it.hasPermissionLevel(4) }
            .then(
                CommandManager.argument("pokemon", PokemonArgumentType.pokemon())
                    .executes { execute(it) })
        dispatcher.register(command)
    }

    private fun execute(context: CommandContext<ServerCommandSource>) : Int {
        val entity = context.source.entity
        if (entity is ServerPlayerEntity && !entity.world.isClient) {
            val pkm = PokemonArgumentType.getPokemon(context, "pokemon")
            val pokemonEntity = PokemonEntity(entity.world as ServerWorld)
            pokemonEntity.let {
                it.pokemon = Pokemon().apply {
                    species = pkm
                    level = 10
                    form = species.forms.first()
                    initialize()
                }
                it.dexNumber.set(it.pokemon.species.nationalPokedexNumber)
            }
            entity.world.spawnEntity(pokemonEntity)
            pokemonEntity.setPosition(entity.pos)
        }
        return Command.SINGLE_SUCCESS
    }

}