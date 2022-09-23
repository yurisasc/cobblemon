package com.cablemc.pokemoncobbled.common.permission

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.permission.PermissionValidator
import net.minecraft.command.CommandSource
import net.minecraft.server.network.ServerPlayerEntity

/**
 * A [PermissionValidator] that always confirms the permission.
 * This is only used when the platform has no concept of permissions.
 *
 */
internal class CobbledPermissionValidator : PermissionValidator {

    override fun initiate() {
        PokemonCobbled.LOGGER.info("Booting CobbledPermissionValidator, permissions will not be checked")
    }

    override fun hasPermission(player: ServerPlayerEntity, permission: String) = true

    override fun hasPermission(source: CommandSource, permission: String) = true

}