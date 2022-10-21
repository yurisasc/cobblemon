/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.command

import com.cablemc.pokemod.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemod.common.battles.BattleFormat
import com.cablemc.pokemod.common.battles.BattleRegistry
import com.cablemc.pokemod.common.battles.BattleSide
import com.cablemc.pokemod.common.battles.actor.MultiPokemonBattleActor
import com.cablemc.pokemod.common.battles.actor.PlayerBattleActor
import com.cablemc.pokemod.common.battles.pokemon.BattlePokemon
import com.cablemc.pokemod.common.pokemon.Pokemon
import com.cablemc.pokemod.common.util.party
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld

object TestCommand {

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val command = CommandManager.literal("testcommand")
            .requires { it.hasPermissionLevel(4) }
            .executes { execute(it) }
        dispatcher.register(command)
    }

    private fun execute(context: CommandContext<ServerCommandSource>): Int {
        if (context.source.entity !is ServerPlayerEntity) {
            return Command.SINGLE_SUCCESS
        }

        try {
            // Player variables
            val player = context.source.entity as ServerPlayerEntity
            val party = player.party()
            party.heal()

            val playerActor = PlayerBattleActor(
                player.uuid,
                party.toBattleTeam()
            )

            // Enemy variables
            val pokemon = Pokemon().apply { species = PokemonSpecies.random() }.also { it.initialize() }
            val enemyPokemon = BattlePokemon(pokemon)

            val enemyPokemon2 = BattlePokemon(PokemonSpecies.random().create())
            val enemyPokemon3 = BattlePokemon(PokemonSpecies.random().create())
            val enemyPokemon4 = BattlePokemon(PokemonSpecies.random().create())


            enemyPokemon.effectedPokemon.sendOut(player.world as ServerWorld, player.pos.add(2.0, 0.0, 0.0))
            enemyPokemon2.effectedPokemon.sendOut(player.world as ServerWorld, player.pos.add(-2.0, 0.0, 0.0))
            enemyPokemon3.effectedPokemon.sendOut(player.world as ServerWorld, player.pos.add(0.0, 0.0, 2.0))
            enemyPokemon4.effectedPokemon.sendOut(player.world as ServerWorld, player.pos.add(0.0, 0.0, -2.0))

            // Start the battle
            BattleRegistry.startBattle(
                battleFormat = BattleFormat.GEN_8_DOUBLES,
                side1 = BattleSide(playerActor),
                side2 = BattleSide(MultiPokemonBattleActor(listOf(enemyPokemon, enemyPokemon2, enemyPokemon3, enemyPokemon4)))
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Command.SINGLE_SUCCESS
    }
}