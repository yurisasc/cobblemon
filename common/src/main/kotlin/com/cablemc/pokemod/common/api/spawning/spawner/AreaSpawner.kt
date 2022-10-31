/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
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
import com.cablemc.pokemod.common.util.squeezeWithinBounds
import net.minecraft.util.math.BlockPos

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
        val constrainedArea = if (area != null) constrainArea(area) else null
        if (constrainedArea != null) {
            //val prospectStart = System.currentTimeMillis()
            val slice = prospector.prospect(this, constrainedArea)
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

    private fun constrainArea(area: SpawningArea): SpawningArea? {
        val basePos = BlockPos(area.baseX, area.baseY, area.baseZ)
        val min = area.world.squeezeWithinBounds(basePos)
        val max = area.world.squeezeWithinBounds(basePos.add(area.length, area.height, area.width))
        return if (area.world.canSetBlock(min) && area.world.canSetBlock(max) && min.y != max.y) {
            return SpawningArea(
                cause = area.cause,
                world = area.world,
                baseX = min.x,
                baseY = min.y,
                baseZ = min.z,
                length = area.length,
                height = max.y - min.y,
                width = area.width
            )
        } else {
            null
        }
    }
}
