package com.cablemc.pokemoncobbled.common.command

import com.cablemc.pokemoncobbled.common.api.moves.Moves
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.battles.BattleFormat
import com.cablemc.pokemoncobbled.common.battles.BattleRegistry
import com.cablemc.pokemoncobbled.common.battles.BattleSide
import com.cablemc.pokemoncobbled.common.battles.actor.MultiPokemonBattleActor
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
            val party = player.party()
            party.heal()

            val playerActor = PlayerBattleActor(
                player.uuid,
                party.toBattleTeam()
            )

            // Enemy variables
            val enemyId = UUID.randomUUID()
            val pokemon = Pokemon().apply { species = PokemonSpecies.MAGIKARP }
            pokemon.moveSet.setMove(0, Moves.getByName("tackle")!!.create())
            pokemon.moveSet.setMove(1, Moves.getByName("aerialace")!!.create())
            pokemon.moveSet.setMove(2, Moves.getByName("airslash")!!.create())
            pokemon.moveSet.setMove(3, Moves.getByName("aurasphere")!!.create())
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