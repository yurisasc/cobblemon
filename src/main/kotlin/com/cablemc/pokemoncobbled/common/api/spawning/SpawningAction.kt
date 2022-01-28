package com.cablemc.pokemoncobbled.common.api.spawning

import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.spawner.Spawner
import net.minecraft.world.entity.Entity

open class SpawningAction<T : Entity>(
    val spawner: Spawner,
    val detail: SpawnDetail,
    val ctx: SpawningContext,
    val entity: T
) {
    open fun doSpawn() {
        entity.setPos(ctx.position)
        ctx.level.addFreshEntity(entity)
        spawner.spawnedEntities.add(entity.uuid)
    }
}