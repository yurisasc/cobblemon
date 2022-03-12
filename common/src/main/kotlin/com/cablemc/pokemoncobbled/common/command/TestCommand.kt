package com.cablemc.pokemoncobbled.common.command

import com.cablemc.pokemoncobbled.common.api.moves.Moves
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.battles.BattleFormat
import com.cablemc.pokemoncobbled.common.battles.BattleRegistry
import com.cablemc.pokemoncobbled.common.battles.BattleSide
import com.cablemc.pokemoncobbled.common.battles.actor.PlayerBattleActor
import com.cablemc.pokemoncobbled.common.battles.actor.PokemonBattleActor
import com.cablemc.pokemoncobbled.common.battles.ai.RandomBattleAI
import com.cablemc.pokemoncobbled.common.battles.pokemon.BattlePokemon
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.party
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

object TestCommand {

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val command = Commands.literal("testcommand")
            .requires { it.hasPermission(4) }
            .executes { execute(it) }
        dispatcher.register(command)
    }

    private fun execute(context: CommandContext<CommandSourceStack>): Int {
        if (context.source.entity !is ServerPlayer) {
            return Command.SINGLE_SUCCESS
        }

        try {
            // Player variables
            val player = context.source.entity as ServerPlayer
            val firstPokemon = player.party().get(0)!!
            firstPokemon.heal()
            val playerActor = PlayerBattleActor(
                player.uuid,
                listOf(BattlePokemon(firstPokemon))
            )

            // Enemy variables
            val enemyId = UUID.randomUUID()
            val pokemon = Pokemon().apply { species = PokemonSpecies.MAGIKARP }
            pokemon.moveSet.setMove(0, Moves.TACKLE.create())
            pokemon.moveSet.setMove(1, Moves.AERIAL_ACE.create())
            pokemon.moveSet.setMove(2, Moves.AIR_SLASH.create())
            pokemon.moveSet.setMove(3, Moves.SPLASH.create())
            val enemyPokemon = PokemonBattleActor(enemyId, BattlePokemon(pokemon))

            // Start the battle
            BattleRegistry.startBattle(
                battleFormat = BattleFormat.GEN_8_MULTI,
                side1 = BattleSide(playerActor, PokemonBattleActor(UUID.randomUUID(), BattlePokemon(PokemonSpecies.BULBASAUR.create()))),
                side2 = BattleSide(enemyPokemon, PokemonBattleActor(UUID.randomUUID(), BattlePokemon(PokemonSpecies.CHARIZARD.create())))
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Command.SINGLE_SUCCESS
    }
}