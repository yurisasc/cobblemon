package com.cablemc.pokemoncobbled.common.command

import com.cablemc.pokemoncobbled.common.api.abilities.extensions.Test
import com.cablemc.pokemoncobbled.common.api.moves.Moves
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.api.storage.PokemonStoreManager
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyStore
import com.cablemc.pokemoncobbled.common.battles.BattleRegistry
import com.cablemc.pokemoncobbled.common.battles.ai.RandomArtificialDecider
import com.cablemc.pokemoncobbled.common.battles.subject.PlayerSubject
import com.cablemc.pokemoncobbled.common.battles.subject.PokemonSubject
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.server.level.ServerPlayer
import java.util.*

object TestCommand {

    fun register(dispatcher : CommandDispatcher<CommandSourceStack>) {
        val command = Commands.literal("testcommand")
            .requires { it.hasPermission(4) }
            .executes { execute(it) }
        dispatcher.register(command)
    }

    private fun execute(context: CommandContext<CommandSourceStack>): Int {
        if(context.source.entity !is ServerPlayer) {
            return Command.SINGLE_SUCCESS
        }
        // Player variables
        val player: ServerPlayer = context.source.entity as ServerPlayer
        val playerSubject = PlayerSubject("p1", player.uuid, PokemonStoreManager.getParty(player))

        // Enemy variables
        val enemyId = UUID.randomUUID()
        val enemyParty = PartyStore(enemyId)
        val pokemon = Pokemon().apply { species = PokemonSpecies.MAGIKARP }
        pokemon.moveSet.setMove(0, Moves.TACKLE.create())
        pokemon.moveSet.setMove(1, Moves.AERIAL_ACE.create())
        pokemon.moveSet.setMove(2, Moves.AIR_SLASH.create())
        pokemon.moveSet.setMove(3, Moves.AURA_SPHERE.create())
        enemyParty.add(pokemon)
        val enemySubject = PokemonSubject("p2", enemyId, enemyParty, RandomArtificialDecider())

        // Start the battle
        BattleRegistry.startBattle(playerSubject, enemySubject)
        return Command.SINGLE_SUCCESS
    }

}