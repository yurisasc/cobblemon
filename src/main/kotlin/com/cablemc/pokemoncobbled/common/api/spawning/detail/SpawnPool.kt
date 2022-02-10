package com.cablemc.pokemoncobbled.common.api.spawning.detail

import com.cablemc.pokemoncobbled.common.api.spawning.condition.PrecalculationResult
import com.cablemc.pokemoncobbled.common.api.spawning.condition.RootPrecalculation
import com.cablemc.pokemoncobbled.common.api.spawning.condition.SpawningPrecalculation
import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.spawner.Spawner

/**
 * A collection of [SpawnDetail]s with precalculation logic for optimization of searches.
 *
 * A single spawn pool may be used for many different [Spawner]s. Note that changing the
 * [details] list will change the spawns for any [Spawner] sharing this pool. If you want
 * to make a change for a pool specifically to one spawner, take a copy of the pool using
 * [copy], and change that spawner's pool using [Spawner.setSpawnPool].
 *
 * @author Hiroku
 * @since February 9th, 2022
 */
class SpawnPool : Iterable<SpawnDetail> {
    val details = mutableListOf<SpawnDetail>()
    var precalculation: PrecalculationResult<*> = RootPrecalculation.generate(details, emptyList())
    val precalculators = mutableListOf<SpawningPrecalculation<*>>()

    override fun iterator() = details.iterator()

    /**
     * Precalculates spawns into hash mappings using the [precalculators] included
     * in this pool. This will speed up retrieval later, and thins the herd of spawns
     * that need to be thoroughly examined when a spawn is occurring. This function
     * will probably be slow, especially if there are many precalculators and spawns.
     */
    fun precalculate() {
        if (precalculators.isEmpty()) {
            precalculation = RootPrecalculation.generate(details, emptyList())
        }

        precalculation = precalculators.first().generate(details, precalculators.subList(1, precalculators.size))
    }

    /**
     * Retrieves the spawns that are precalculated as being potentially spawns at
     * this context. This, at most, prunes some spawns that were definitely not
     * possible here. The returned list can and almost certainly will include more
     * spawns that are not possible for this context - this function is simple
     * to leverage the precalculation to get a smaller list of spawns as quickly
     * as possible.
     */
    fun retrieve(ctx: SpawningContext): List<SpawnDetail> {
        return precalculation.retrieve(ctx)
    }

    /**
     * Creates a de-referenced copy of the pool which can be modified safely without
     * this pool being changed.
     */
    fun copy(): SpawnPool {
        val copy = SpawnPool()
        copy.details.addAll(details)
        copy.precalculators.addAll(precalculators)
        copy.precalculation = precalculation
        return copy
    }
}