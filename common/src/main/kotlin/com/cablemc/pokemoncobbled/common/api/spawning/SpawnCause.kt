package com.cablemc.pokemoncobbled.common.api.spawning

import com.cablemc.pokemoncobbled.common.api.spawning.spawner.Spawner
import net.minecraft.entity.Entity

open class SpawnCause(
    val spawner: Spawner,
    val bucket: SpawnBucket,
    val entity: Entity? = null
)