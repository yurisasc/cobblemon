package com.cablemc.pokemoncobbled.common.command

import com.cablemc.pokemoncobbled.common.battles.BattleRegistry
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.Util
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.TextComponent
import net.minecraft.server.level.ServerPlayer

object SwitchCommand {

    fun register(dispatcher : CommandDispatcher<CommandSourceStack>) {
        val command = Commands.literal("switch")
            .requires { it.hasPermission(4) }
            .then(
                Commands.argument("pokemon", IntegerArgumentType.integer(1, 4))
                    .executes { execute(it) })
        dispatcher.register(command)
    }

    private fun execute(context: CommandContext<CommandSourceStack>) : Int {
        val entity = context.source.entity
        if (entity is ServerPlayer && !entity.level.isClientSide) {
            val battle = BattleRegistry.getBattleByParticipatingPlayer(entity)
            if(battle != null) {
                val actor = battle.getActor(entity.uuid)
                if(actor != null)
                    battle.writeShowdownAction(">${actor.showdownId} switch ${context.getArgument("pokemon", Integer::class.java)}")
            } else {
                entity.sendMessage(TextComponent("You're not currently in a battle!"), Util.NIL_UUID)
            }
        }
        return Command.SINGLE_SUCCESS
    }

}