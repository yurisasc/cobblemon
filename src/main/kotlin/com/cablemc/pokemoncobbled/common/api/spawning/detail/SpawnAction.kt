package com.cablemc.pokemoncobbled.common.api.spawning.detail

import com.cablemc.pokemoncobbled.common.api.reactive.SimpleObservable
import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.spawner.Spawner
import net.minecraft.world.entity.Entity

/**
 * A scheduled spawning action.
 *
 * @author Hiroku
 * @since February 4th, 2022
 */
abstract class SpawnAction<T : Entity>(
    val spawner: Spawner,
    val ctx: SpawningContext,
    val detail: SpawnDetail
) {
    abstract fun run()

    /**
     * An observable for the entity that will eventually be spawned by this action.
     *
     * You can safely register subscriptions here for anything you want to do to the
     * entity after it's spawned, whenever that may be. This is tolerant of situations
     * where the entity spawn has been canceled, so that your action will not occur.
     */
    val entity: SimpleObservable<T> = SimpleObservable<T>().apply {
        subscribe { entity -> ctx.afterSpawn(entity) }
    }
}