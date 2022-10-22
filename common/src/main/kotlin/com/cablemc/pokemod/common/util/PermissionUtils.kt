/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.util

import com.cablemc.pokemod.common.Pokemod
import com.cablemc.pokemod.common.api.permission.PermissionLevel
import com.mojang.brigadier.builder.ArgumentBuilder
import net.minecraft.command.CommandSource
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity

fun ServerPlayerEntity.hasPermission(permission: String) = Pokemod.permissionValidator.hasPermission(this, permission)

fun CommandSource.hasPermission(permission: String) = Pokemod.permissionValidator.hasPermission(this, permission)

/**
 * Creates an [ArgumentBuilder.requirement] for a permission.
 * If you'd like to apply a requirement as well from a single function use [requiresWithPermission].
 *
 * @param T the type of the [ArgumentBuilder].
 * @param permission The literal permission for this command
 * @param appendRequirement If the existing [ArgumentBuilder.requirement] should be appended to this permission as a single predicate. Defaults to true
 * @return the [ArgumentBuilder].
 */
fun <T : ArgumentBuilder<ServerCommandSource, T>> ArgumentBuilder<ServerCommandSource, T>.permission(permission: String, appendRequirement: Boolean = true): T {
    val permissionPredicate = { src: ServerCommandSource -> Pokemod.permissionValidator.hasPermission(src, permission)  }
    return if (appendRequirement) this.requires(this.requirement.and(permissionPredicate)) else this.requires(permissionPredicate)
}

/**
 * Creates an [ArgumentBuilder.requirement] merged with a permission.
 *
 * @param T the type of the [ArgumentBuilder].
 * @param permission The literal permission for this command
 * @param predicate The requirement for the command.
 * @return the [ArgumentBuilder].
 */
fun <T : ArgumentBuilder<ServerCommandSource, T>> ArgumentBuilder<ServerCommandSource, T>.requiresWithPermission(permission: String, predicate: (src: ServerCommandSource) -> Boolean): T {
    this.requires(predicate)
    return this.permission(permission)
}

/**
 * Appends a permission level check to the existing [ArgumentBuilder.requirement].
 *
 * @param T the type of the [ArgumentBuilder].
 * @param level The [PermissionLevel] for this command.
 * @return the [ArgumentBuilder].
 */
fun <T : ArgumentBuilder<ServerCommandSource, T>> ArgumentBuilder<ServerCommandSource, T>.permissionLevel(level: PermissionLevel): T = this.requires(this.requirement.and { it.hasPermissionLevel(level.numericalValue) })