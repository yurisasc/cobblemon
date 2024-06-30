/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.util

import com.google.gson.JsonElement
import com.mojang.serialization.JsonOps
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtOps
import net.minecraft.server.network.ServerPlayerEntity

fun ItemStack.saveToJson(): JsonElement = JsonOps.INSTANCE.withEncoder(ItemStack.CODEC).apply(this).getOrThrow {
    return@getOrThrow IllegalStateException("Cant serialize ItemStack")
}
fun ItemStack.isHeld(player: ServerPlayerEntity) = this in player.handItems && !isEmpty