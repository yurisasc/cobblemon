
package com.cobblemon.mod.common.command


import com.cobblemon.mod.common.api.permission.CobblemonPermissions
import com.cobblemon.mod.common.battles.BattleRegistry
import com.cobblemon.mod.common.util.alias
import com.cobblemon.mod.common.util.permission
import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource

object AbandonMultiTeam {

    private const val ALIAS = "abandonmultibattleteam"

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val command = dispatcher.register(
                literal("abandonmultiteam")
                        .permission(CobblemonPermissions.ABANDON_MULTITEAM)
                        .executes { execute(it,) }
        )
        dispatcher.register(command.alias(ALIAS))
    }

    private fun execute(context: CommandContext<ServerCommandSource>) : Int {

        val player = context.source.player
        if (player != null) {
            BattleRegistry.removeTeamMember(player)
        }
        return SINGLE_SUCCESS
    }

}