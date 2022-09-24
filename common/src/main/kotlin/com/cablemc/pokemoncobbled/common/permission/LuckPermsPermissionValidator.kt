package com.cablemc.pokemoncobbled.common.permission

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.permission.PermissionValidator
import net.luckperms.api.LuckPermsProvider
import net.minecraft.command.CommandSource
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity

class LuckPermsPermissionValidator : PermissionValidator {

    private val luckPerms by lazy { LuckPermsProvider.get() }

    override fun initiate() {
        PokemonCobbled.LOGGER.info("Booting LuckPermsPermissionValidator, permissions will be checked through LuckPerms, see https://luckperms.net/ for more information")
    }

    override fun hasPermission(player: ServerPlayerEntity, permission: String) = this.luckPerms.userManager.getUser(player.uuid)?.cachedData?.permissionData?.checkPermission(permission)?.asBoolean() ?: false

    override fun hasPermission(source: CommandSource, permission: String): Boolean {
        val serverSource = source as? ServerCommandSource ?: return true
        val player = source.entity as? ServerPlayerEntity ?: return true
        return this.hasPermission(player, permission)
    }

}