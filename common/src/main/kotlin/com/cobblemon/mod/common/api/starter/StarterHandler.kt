/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.starter

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

interface StarterHandler {
    fun getStarters(player: ServerPlayerEntity): Map<Identifier, StarterCategory>
    fun handleJoin(player: ServerPlayerEntity)
    fun requestStarterChoice(player: ServerPlayerEntity)
    fun chooseStarter(player: ServerPlayerEntity, categoryId: Identifier, index: Int)
}