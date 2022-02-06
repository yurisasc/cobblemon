package com.cablemc.pokemoncobbled.common.api.spawning.selection

import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import com.cablemc.pokemoncobbled.common.api.spawning.spawner.Spawner

/**
 * Interface responsible for taking all of the potential spawns across many contexts, and applying some kind of
 * selection process to choose one. It is also responsible for generating a name to percentage probability for the given
 * spawn information for checking spawns under specific conditions.
 *
 * @author Hiroku
 * @since January 31st, 2022
 */
interface SpawningSelector {
    fun select(spawner: Spawner, contexts: List<SpawningContext>, spawnDetails: List<SpawnDetail>): Pair<SpawningContext, SpawnDetail>?
    fun getProbabilities(spawner: Spawner, contexts: List<SpawningContext>, spawnDetails: List<SpawnDetail>): Map<String, Float>
}