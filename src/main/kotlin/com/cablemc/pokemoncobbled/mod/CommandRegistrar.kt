package com.cablemc.pokemoncobbled.mod

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.mojang.brigadier.Command.SINGLE_SUCCESS
import net.minecraft.commands.Commands
import net.minecraft.server.level.ServerLevel
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object CommandRegistrar {
    @SubscribeEvent
    fun on(event: RegisterCommandsEvent) {
        var pokemonEntity: PokemonEntity? = null
        val command = Commands.literal("runtest")
            .executes { cmdSrc ->
                val player = cmdSrc.source.playerOrException
                pokemonEntity = PokemonEntity(player.level as ServerLevel)
                pokemonEntity?.let {
                    it.pokemon = Pokemon().apply { species = PokemonSpecies.species.random() }
                    it.dexNumber.set(it.pokemon.species.nationalPokedexNumber)
                }
                player.level.addFreshEntity(pokemonEntity!!)
                pokemonEntity!!.setPos(player.position())
                return@executes SINGLE_SUCCESS
            }

        event.dispatcher.register(command)
    }
}