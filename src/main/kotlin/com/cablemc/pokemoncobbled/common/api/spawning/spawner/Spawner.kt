package com.cablemc.pokemoncobbled.common.api.spawning.spawner

import com.cablemc.pokemoncobbled.common.api.spawning.condition.FinalPrecalculationResult
import com.cablemc.pokemoncobbled.common.api.spawning.condition.PrecalculationResult
import com.cablemc.pokemoncobbled.common.api.spawning.condition.RootPrecalculation
import com.cablemc.pokemoncobbled.common.api.spawning.condition.SpawningPrecalculation
import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import java.util.UUID

/**
 * Interface representing something that performs the action of spawning. Various functions
 * exist to streamline the process of using the Best Spawner API.
 *
 * @author Hiroku
 * @since January 24th, 2022
 */
interface Spawner {
    val spawnedEntities: MutableList<UUID>
    val spawnDetails: MutableList<SpawnDetail>
    val precalculators: MutableList<SpawningPrecalculation<*>>
    fun getPrecalculationResult(): PrecalculationResult<*>
    fun setPrecalculationResult(precalculation: PrecalculationResult<*>)

    fun run(ctx: SpawningContext) {
        if (canSpawn()) {

        }
        // TODO DO TOO
    }


    fun canSpawn(): Boolean

    fun getMatchingSpawns(ctx: SpawningContext) = getPrecalculationResult().retrieve(ctx).filter { it.isSatisfiedBy(ctx) }

    fun registerPrecalculator(precalculation: SpawningPrecalculation<*>) {
        precalculators.add(precalculation)
    }

    fun precalculate() {
        if (precalculators.isEmpty()) {
            setPrecalculationResult(
                FinalPrecalculationResult<Any>(
                    calculation = RootPrecalculation,
                    mapping = mutableMapOf(Unit to spawnDetails)
                )
            )
        }

        setPrecalculationResult(precalculators.first().generate(spawnDetails, precalculators.subList(1, precalculators.size)))
    }
}