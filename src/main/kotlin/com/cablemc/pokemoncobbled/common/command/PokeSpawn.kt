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
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player

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
        val entity = context.source.entity
        if(entity is ServerPlayer && !entity.level.isClientSide) {
            val speciesName = StringArgumentType.getString(context, "species")
            val speciesArg = PokemonSpecies.getByName(speciesName) ?: PokemonSpecies.species.random()
            val player = context.source.playerOrException
            val pokemonEntity = PokemonEntity(player.level as ServerLevel)
            pokemonEntity.let {
                it.pokemon = Pokemon().apply {
                    species = speciesArg
                    form = species.forms.first()
                }
                it.dexNumber.set(it.pokemon.species.nationalPokedexNumber)
                it.pokemon.scaleModifier = 0.8f
                it.scaleModifier.set(it.pokemon.scaleModifier)
            }
            pokemonEntity.refreshDimensions()
            player.level.addFreshEntity(pokemonEntity)
            pokemonEntity.setPos(player.position())
        }
        return 1
    }

}