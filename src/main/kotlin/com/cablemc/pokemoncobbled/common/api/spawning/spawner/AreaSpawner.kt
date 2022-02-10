package com.cablemc.pokemoncobbled.common.api.spawning.spawner

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.spawning.SpawnerManager
import com.cablemc.pokemoncobbled.common.api.spawning.context.AreaContextResolver
import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.context.calculators.AreaSpawningContextCalculator
import com.cablemc.pokemoncobbled.common.api.spawning.context.calculators.SpawningContextCalculator.Companion.prioritizedAreaCalculators
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnPool
import com.cablemc.pokemoncobbled.common.api.spawning.prospecting.SpawningProspector

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
    abstract fun getArea(): SpawningArea?

    var prospector: SpawningProspector = PokemonCobbled.prospector
    var resolver: AreaContextResolver = PokemonCobbled.areaContextResolver
    var contextCalculators: List<AreaSpawningContextCalculator<*>> = prioritizedAreaCalculators

    override fun run(): Pair<SpawningContext, SpawnDetail>? {
        val area = getArea()
        if (area != null) {
            val slice = prospector.prospect(this, area)
            val contexts = resolver.resolve(this, contextCalculators, slice)
            return getSpawningSelector().select(this, contexts, getSpawnPool().details)
        }

        return null
    }
}