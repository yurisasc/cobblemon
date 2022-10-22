/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.battles.model.actor

import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Vec3d

interface FleeableBattleActor {
    val fleeDistance: Float
    fun getWorldAndPosition(): Pair<ServerWorld, Vec3d>?
}