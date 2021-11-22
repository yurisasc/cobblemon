package com.cablemc.pokemoncobbled.mod

import com.mojang.brigadier.Command.SINGLE_SUCCESS
import net.minecraft.commands.Commands
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.MobSpawnType
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object CommandRegistrar {
    @SubscribeEvent
    fun on(event: RegisterCommandsEvent) {
        val command = Commands.literal("runtest")
            .executes { cmdSrc ->
                println("hello")
                val player = cmdSrc.source.playerOrException
                val pokemonResource = PokemonCobbledMod.entityRegistry.POKEMON
                val pokemonType = pokemonResource.get()
                player.level.addFreshEntity(pokemonType.spawn(player.level as ServerLevel, null, null, null, player.onPos, MobSpawnType.COMMAND, true, true)!!)
                return@executes SINGLE_SUCCESS
            }

        event.dispatcher.register(command)
    }
}