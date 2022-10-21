/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.spawning.spawner

import com.cablemc.pokemod.common.api.spawning.SpawnCause
import com.cablemc.pokemod.common.api.spawning.SpawnerManager
import com.cablemc.pokemod.common.api.spawning.detail.SpawnPool
import com.cablemc.pokemod.common.util.squeezeWithinBounds
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * A spawner that works within a fixed area, and ticks on its own. The location
 * and size of the area is necessary for construction, but after that this spawner
 * can be registered in a [SpawnerManager] and left to its own devices.
 *
 * @author Hiroku
 * @since February 5th, 2022
 */
open class FixedAreaSpawner(
    name: String,
    spawns: SpawnPool,
    manager: SpawnerManager,
    val world: World,
    val position: BlockPos,
    val horizontalRadius: Int,
    val verticalRadius: Int
) : AreaSpawner(name, spawns, manager) {
    override fun getArea(cause: SpawnCause): SpawningArea? {
        val min = world.squeezeWithinBounds(position.add(-horizontalRadius, -verticalRadius, -horizontalRadius))
        val max = world.squeezeWithinBounds(position.add(horizontalRadius, verticalRadius, horizontalRadius))

        return if (world.canSetBlock(min) && world.canSetBlock(max)) {
            SpawningArea(
                cause = cause,
                world = world,
                baseX = min.x,
                baseY = min.y,
                baseZ = min.z,
                length = horizontalRadius * 2 + 1,
                height = verticalRadius * 2 + 1,
                width = horizontalRadius * 2 + 1
            )
        } else {
            null
        }
    }
}