package com.cablemc.pokemoncobbled.common.command

import com.cablemc.pokemoncobbled.common.api.moves.Moves
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.battles.BattleFormat
import com.cablemc.pokemoncobbled.common.battles.BattleRegistry
import com.cablemc.pokemoncobbled.common.battles.BattleSide
import com.cablemc.pokemoncobbled.common.battles.actor.MultiPokemonBattleActor
import com.cablemc.pokemoncobbled.common.battles.actor.PlayerBattleActor
import com.cablemc.pokemoncobbled.common.battles.pokemon.BattlePokemon
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonBehaviourFlag
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.party
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity

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
            party.find { it.species === PokemonSpecies.CHARIZARD }?.let {
                it.entity?.let { entity ->
                    entity.setBehaviourFlag(PokemonBehaviourFlag.EXCITED, !entity.getBehaviourFlag(PokemonBehaviourFlag.EXCITED))
                }
            }
            party.heal()

            val playerActor = PlayerBattleActor(
                player.uuid,
                party.toBattleTeam()
            )

            // Enemy variables
            val pokemon = Pokemon().apply { species = PokemonSpecies.MAGIKARP }
            pokemon.moveSet.add(Moves.getByName("splash")!!.create()) // TODO remove when move loading works properly
            val enemyPokemon = BattlePokemon(pokemon)

            val enemyPokemon2 = BattlePokemon(PokemonSpecies.BLASTOISE.create())
            val enemyPokemon3 = BattlePokemon(PokemonSpecies.BUTTERFREE.create())
            val enemyPokemon4 = BattlePokemon(PokemonSpecies.DIGLETT.create())

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