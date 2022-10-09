/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.permission

import com.cablemc.pokemod.common.Pokemod
import com.cablemc.pokemod.common.api.permission.PermissionValidator
import net.minecraft.command.CommandSource
import net.minecraft.server.network.ServerPlayerEntity

/**
 * A [PermissionValidator] that always confirms the permission.
 * This is only used when the platform has no concept of permissions.
 */
class LaxPermissionValidator : PermissionValidator {

    override fun initialize() {
        Pokemod.LOGGER.info("Booting LaxPermissionValidator, permissions will not be checked")
    }

    override fun hasPermission(player: ServerPlayerEntity, permission: String) = true
    override fun hasPermission(source: CommandSource, permission: String) = true
}