package com.cablemc.pokemoncobbled.common.api.spawning

import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import com.cablemc.pokemoncobbled.common.api.spawning.spawner.Spawner
import com.cablemc.pokemoncobbled.common.util.toVec3
import net.minecraft.world.entity.Entity

/**
 * A scheduled spawning action. Needs some rewriting with subclasses for providing the entity ):
 *
 * @author Hiroku
 * @since January 30th, 2022
 */
open class SpawningAction<T : Entity>(
    val spawner: Spawner,
    val detail: SpawnDetail,
    val ctx: SpawningContext,
    val entity: T
) {
    open fun doSpawn() {
        entity.setPos(ctx.position.toVec3().add(0.5, 0.0, 0.5))
        ctx.level.addFreshEntity(entity)
        spawner.spawnedEntities.add(entity.uuid)
    }
}