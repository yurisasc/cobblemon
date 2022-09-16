/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api.spawning

import com.cablemc.pokemoncobbled.common.api.spawning.condition.BucketPrecalculation
import com.cablemc.pokemoncobbled.common.api.spawning.condition.ContextPrecalculation
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnPool

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
    lateinit var WORLD_SPAWN_POOL: SpawnPool

    fun load() {
        WORLD_SPAWN_POOL = SpawnLoader.load("world").addPrecalculators(ContextPrecalculation, BucketPrecalculation)
    }
}