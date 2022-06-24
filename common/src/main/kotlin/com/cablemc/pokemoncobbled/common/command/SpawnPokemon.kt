package com.cablemc.pokemoncobbled.common.command

import com.cablemc.pokemoncobbled.common.command.argument.PokemonPropertiesArgumentType
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity

object SpawnPokemon {

    fun register(dispatcher : CommandDispatcher<ServerCommandSource>) {
        val command = dispatcher.register(literal("spawnpokemon")
            .requires { it.hasPermissionLevel(4) }
            .then(
                CommandManager.argument("pokemon", PokemonPropertiesArgumentType.properties())
                    .executes { execute(it) }
            ))
        dispatcher.register(literal("pokespawn").redirect(command))
    }

    private fun execute(context: CommandContext<ServerCommandSource>) : Int {
        val entity = context.source.entity
        if (entity is ServerPlayerEntity && !entity.world.isClient) {
            val pkm = PokemonPropertiesArgumentType.getPokemonProperties(context, "pokemon")
            val pokemonEntity = pkm.createEntity(entity.world)
            entity.world.spawnEntity(pokemonEntity)
            pokemonEntity.setPosition(entity.pos)
        }
        return Command.SINGLE_SUCCESS
    }

}