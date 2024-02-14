package com.cobblemon.mod.common.api.spawning.fallingstar

import com.cobblemon.mod.common.api.spawning.SpawnBucket
import com.cobblemon.mod.common.api.spawning.SpawnCause
import com.cobblemon.mod.common.api.spawning.spawner.Spawner
import net.minecraft.entity.Entity

class FallingStarSpawnCause(
    spawner: Spawner,
    bucket: SpawnBucket,
    entity: Entity?
) : SpawnCause(spawner, bucket, entity)