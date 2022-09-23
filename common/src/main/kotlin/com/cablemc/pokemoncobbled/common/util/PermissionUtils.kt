package com.cablemc.pokemoncobbled.common.util

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.permission.PermissionValidator
import com.mojang.brigadier.builder.ArgumentBuilder
import net.minecraft.command.CommandSource
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity

fun ServerPlayerEntity.hasPermission(permission: String) = PokemonCobbled.permissionValidator.hasPermission(this, permission)

fun CommandSource.hasPermission(permission: String) = PokemonCobbled.permissionValidator.hasPermission(this, permission)

/**
 * Creates an [ArgumentBuilder.requirement] for a permission.
 * Keep in mind that if you require a more complex requirement you cannot use this extension as requirements are singular predicates and not a collection.
 * See [PermissionValidator] and [PokemonCobbled.permissionValidator].
 *
 * @param T the type of the [ArgumentBuilder].
 * @param permission The literal permission for this command
 * @return the [ArgumentBuilder].
 */
fun <T : ArgumentBuilder<ServerCommandSource, T>> ArgumentBuilder<ServerCommandSource, T>.permission(permission: String): T = requires { PokemonCobbled.permissionValidator.hasPermission(it, permission) }