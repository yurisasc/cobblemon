package com.cablemc.pokemoncobbled.common.api.spawning.spawner

import com.cablemc.pokemoncobbled.common.api.spawning.SpawnerManager
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnPool
import com.cablemc.pokemoncobbled.common.util.squeezeWithinBounds
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level

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
    val level: Level,
    val position: BlockPos,
    val horizontalRadius: Int,
    val verticalRadius: Int
) : AreaSpawner(name, spawns, manager) {
    override fun getArea(): SpawningArea? {
        val min = level.squeezeWithinBounds(position.offset(-horizontalRadius, -verticalRadius, -horizontalRadius))
        val max = level.squeezeWithinBounds(position.offset(horizontalRadius, verticalRadius, horizontalRadius))

        return if (level.isLoaded(min) && level.isLoaded(max)) {
            SpawningArea(
                cause = this,
                level = level,
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