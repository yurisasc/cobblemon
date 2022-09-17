/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

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
    fun select(spawner: Spawner, contexts: List<SpawningContext>): Pair<SpawningContext, SpawnDetail>?

    fun getProbabilities(spawner: Spawner, contexts: List<SpawningContext>): Map<SpawnDetail, Float> {
        val weights = getTotalWeights(spawner, contexts)
        val totalWeight = weights.values.sum()
        val percentages = mutableMapOf<SpawnDetail, Float>()
        weights.forEach { (spawnDetail, weight) -> percentages[spawnDetail] = (weight / totalWeight * 100F).coerceIn(0F..100F) }
        return percentages
    }

    fun getTotalWeights(spawner: Spawner, contexts: List<SpawningContext>): Map<SpawnDetail, Float>

}