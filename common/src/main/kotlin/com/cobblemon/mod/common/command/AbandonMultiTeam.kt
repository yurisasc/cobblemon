
package com.cobblemon.mod.common.command


import com.cobblemon.mod.common.api.permission.CobblemonPermissions
import com.cobblemon.mod.common.battles.BattleRegistry
import com.cobblemon.mod.common.util.alias
import com.cobblemon.mod.common.util.commandLang
import com.cobblemon.mod.common.util.permission
import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

object AbandonMultiTeam {

    private const val ALIAS = "abandonmultibattleteam"

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val command = dispatcher.register(
                Commands.literal("abandonmultiteam")
                        .permission(CobblemonPermissions.ABANDON_MULTITEAM)
                        .executes { execute(it,) }
        )
        dispatcher.register(command.alias(ALIAS))
    }

    private fun execute(context: CommandContext<CommandSourceStack>) : Int {

        val player = context.source.player
        if (player != null) {
            BattleRegistry.removeTeamMember(player)
        }
        return SINGLE_SUCCESS
    }

}