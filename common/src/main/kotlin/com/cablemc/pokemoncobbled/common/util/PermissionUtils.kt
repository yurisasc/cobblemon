package com.cablemc.pokemoncobbled.common.util

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.mojang.brigadier.builder.ArgumentBuilder
import net.minecraft.command.CommandSource
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity

fun ServerPlayerEntity.hasPermission(permission: String) = PokemonCobbled.permissionValidator.hasPermission(this, permission)

fun CommandSource.hasPermission(permission: String) = PokemonCobbled.permissionValidator.hasPermission(this, permission)

fun <T : ArgumentBuilder<ServerCommandSource, T>> ArgumentBuilder<ServerCommandSource, T>.permission(permission: String): T = requires { PokemonCobbled.permissionValidator.hasPermission(it, permission) }