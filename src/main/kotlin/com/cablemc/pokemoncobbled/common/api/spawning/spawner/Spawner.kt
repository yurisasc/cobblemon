package com.cablemc.pokemoncobbled.common.api.spawning.spawner

import com.cablemc.pokemoncobbled.common.api.spawning.SpawnDetail
import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.context.calculators.SpawningContextCalculator
import java.util.UUID

interface Spawner {
    val spawnedEntities: MutableList<UUID>
    val spawnDetails: MutableList<SpawnDetail>

    fun run(ctx: SpawningContext) {
        if (canSpawn()) {

        }
        // TODO DO TOO
    }


    fun canSpawn(): Boolean
    fun getMatchingSpawns(ctx: SpawningContext) = spawnDetails.filter { it.isSatisfiedBy(ctx) }
}