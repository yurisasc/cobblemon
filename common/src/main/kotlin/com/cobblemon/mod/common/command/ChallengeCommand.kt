package com.cobblemon.mod.common.command

import com.cobblemon.mod.common.battles.BattleBuilder
import com.cobblemon.mod.common.util.party
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

object ChallengeCommand {

    fun register(dispatcher : CommandDispatcher<ServerCommandSource>) {
        val command = CommandManager.literal("battle")
            .then(
                CommandManager
                    .argument("target", EntityArgumentType.player())
                    .executes(::execute)
            )

        dispatcher.register(command)
    }

    private fun execute(context: CommandContext<ServerCommandSource>) : Int {
        val player = context.source.playerOrThrow
        val target = EntityArgumentType.getPlayer(context, "target")

        val leadingPlayerPokemon = player.party().filterNotNull().firstOrNull() ?: return 0
        val leadingTargetPokemon = target.party().filterNotNull().firstOrNull() ?: return 0
        BattleBuilder.pvp1v1(player, target, leadingPlayerPokemon.uuid, leadingTargetPokemon.uuid)
        return Command.SINGLE_SUCCESS
    }

}