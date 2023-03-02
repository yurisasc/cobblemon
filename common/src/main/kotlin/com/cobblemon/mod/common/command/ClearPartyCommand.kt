package com.cobblemon.mod.common.command

import com.cobblemon.mod.common.api.permission.CobblemonPermissions
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.util.party
import com.cobblemon.mod.common.util.permission
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity

object ClearPartyCommand {

    private const val NAME = "clearparty"
    private const val PLAYER = "player"

    fun register(dispatcher : CommandDispatcher<ServerCommandSource>) {
        val command = CommandManager.literal(NAME)
             .permission(CobblemonPermissions.CLEAR_PARTY)
             .then( CommandManager.argument(PLAYER, EntityArgumentType.players()).executes { execute(it) }
                )
        dispatcher.register(command)
    }

    private fun execute(context: CommandContext<ServerCommandSource>) : Int {
        val target = EntityArgumentType.getPlayer(context, "player")
        val party = target.party()

        val pokemon = party.find{it != null}
        if (pokemon == null) {
            context.source.sendError("There is no Pokemon in $target's Party".text())
            return 0
        }

        party.remove(pokemon)
        if (context.source.entity != target) {
            if (context.source.entity is ServerPlayerEntity) {
                val player = context.source.player ?: return Command.SINGLE_SUCCESS
                context.source.sendFeedback("You Cleared $player's Party".text(), true)
                return Command.SINGLE_SUCCESS
            }
        }
        return Command.SINGLE_SUCCESS
    }
}