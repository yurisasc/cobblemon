/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.starter

import com.cablemc.pokemod.common.config.starter.StarterCategory
import net.minecraft.server.network.ServerPlayerEntity

interface StarterHandler {
    fun getStarterList(player: ServerPlayerEntity): List<StarterCategory>
    fun handleJoin(player: ServerPlayerEntity)
    fun requestStarterChoice(player: ServerPlayerEntity)
    fun chooseStarter(player: ServerPlayerEntity, categoryName: String, index: Int)
}