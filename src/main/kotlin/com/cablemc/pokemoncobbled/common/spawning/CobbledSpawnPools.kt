package com.cablemc.pokemoncobbled.common.spawning

import com.cablemc.pokemoncobbled.common.api.spawning.SpawnLoader
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnPool
import com.cablemc.pokemoncobbled.common.util.cobbledResource

/**
 * A collection of all of Pokémon Cobbled's general-purpose [SpawnPool]s. These
 * are referenced by Cobbled spawner implementations. Updating these will update
 * the spawns across the entire mod.
 *
 * @author Hiroku
 * @since February 10th, 2022
 */
object CobbledSpawnPools {
    /** [SpawnPool] used for standard world spawning. */
    var WORLD_SPAWN_POOL = SpawnLoader.load("world")
}