/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.spawning.spawner

import com.cablemc.pokemod.common.Pokemod
import com.cablemc.pokemod.common.api.spawning.SpawnCause
import com.cablemc.pokemod.common.api.spawning.SpawnerManager
import com.cablemc.pokemod.common.api.spawning.context.AreaContextResolver
import com.cablemc.pokemod.common.api.spawning.context.SpawningContext
import com.cablemc.pokemod.common.api.spawning.context.calculators.AreaSpawningContextCalculator
import com.cablemc.pokemod.common.api.spawning.context.calculators.SpawningContextCalculator.Companion.prioritizedAreaCalculators
import com.cablemc.pokemod.common.api.spawning.detail.SpawnDetail
import com.cablemc.pokemod.common.api.spawning.detail.SpawnPool
import com.cablemc.pokemod.common.api.spawning.prospecting.SpawningProspector

/**
 * A type of [TickingSpawner] that operates within some area. When this spawner type
 * is told to do a spawning action, the subclass can provide a [SpawningArea] to use.
 * If a non-null value is returned, the [prospector] and [resolver] will be used to
 * select a spawn and action it.
 *
 * Subclasses must implement the function retrieving what area to do the spawning in,
 * but otherwise this class is feature-complete.
 *
 * @author Hiroku
 * @since February 5th, 2022
 */
abstract class AreaSpawner(
    name: String,
    spawns: SpawnPool,
    manager: SpawnerManager
) : TickingSpawner(name, spawns, manager) {
    abstract fun getArea(cause: SpawnCause): SpawningArea?

    var prospector: SpawningProspector = Pokemod.prospector
    var resolver: AreaContextResolver = Pokemod.areaContextResolver
    var contextCalculators: List<AreaSpawningContextCalculator<*>> = prioritizedAreaCalculators

    override fun run(cause: SpawnCause): Pair<SpawningContext, SpawnDetail>? {
        val area = getArea(cause)
        if (area != null) {
            //val prospectStart = System.currentTimeMillis()
            val slice = prospector.prospect(this, area)
            //val prospectEnd = System.currentTimeMillis()
            val contexts = resolver.resolve(this, contextCalculators, slice)
            //val resolveEnd = System.currentTimeMillis()
            //val prospectDuration = prospectEnd - prospectStart
            //val resolveDuration = resolveEnd - prospectEnd
            //println("Prospecting took: $prospectDuration ms. Resolution took: $resolveDuration ms")
            // Takes about 3ms on my laptop to prospect, similar to context resolve - not very good, needs some thought
            return getSpawningSelector().select(this, contexts)
        }

        return null
    }
}
