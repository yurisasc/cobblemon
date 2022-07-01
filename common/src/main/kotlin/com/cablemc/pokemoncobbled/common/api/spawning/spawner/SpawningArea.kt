package com.cablemc.pokemoncobbled.common.api.spawning.spawner

import com.cablemc.pokemoncobbled.common.api.spawning.SpawnCause
import com.cablemc.pokemoncobbled.common.api.spawning.WorldSlice
import net.minecraft.world.World

/**
 * An area in which to slice out a [WorldSlice].
 *
 * @author Hiroku
 * @since February 5th, 2022
 */
data class SpawningArea(
    val cause: SpawnCause,
    val world: World,
    val baseX: Int,
    val baseY: Int,
    val baseZ: Int,
    val length: Int,
    val height: Int,
    val width: Int
)