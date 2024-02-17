/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.spawning.context

import com.cobblemon.mod.common.api.spawning.fallingstar.FallingStarSpawnCause
import com.cobblemon.mod.common.api.spawning.influence.SpawningInfluence
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

class FallingStarSpawningContext(
    cause: FallingStarSpawnCause,
    world: ServerWorld,
    pos: BlockPos,
    influences: MutableList<SpawningInfluence>
) : TriggerSpawningContext(
    cause = cause,
    world = world,
    position = pos,
    light = world.getLightLevel(pos),
    skyLight = world.getLightLevel(pos.up()),
    canSeeSky = world.isSkyVisibleAllowingSea(pos),
    influences = influences
) {

}