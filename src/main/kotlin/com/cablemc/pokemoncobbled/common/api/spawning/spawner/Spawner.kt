package com.cablemc.pokemoncobbled.common.api.spawning.spawner

import com.cablemc.pokemoncobbled.common.api.spawning.condition.FinalPrecalculationResult
import com.cablemc.pokemoncobbled.common.api.spawning.condition.PrecalculationResult
import com.cablemc.pokemoncobbled.common.api.spawning.condition.RootPrecalculation
import com.cablemc.pokemoncobbled.common.api.spawning.condition.SpawningPrecalculation
import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import com.cablemc.pokemoncobbled.common.api.spawning.influence.SpawningInfluence
import com.cablemc.pokemoncobbled.common.api.spawning.selection.SpawningSelector
import net.minecraft.world.entity.Entity
import java.util.concurrent.Executors

/**
 * Interface representing something that performs the action of spawning. Various functions
 * exist to streamline the process of using the Best Spawner API.
 *
 * @author Hiroku
 * @since January 24th, 2022
 */
interface Spawner {
    companion object {
        var worker = Executors.newSingleThreadExecutor { r -> Thread(r, "Spawning Worker") }
    }

    val name: String
    val precalculators: MutableList<SpawningPrecalculation<*>>
    val influences: MutableList<SpawningInfluence>
    fun getPrecalculationResult(): PrecalculationResult<*>?
    fun setPrecalculationResult(precalculation: PrecalculationResult<*>)
    fun getSpawningSelector(): SpawningSelector
    fun setSpawningSelector(selector: SpawningSelector)
    fun getSpawnDetails(): MutableList<SpawnDetail>
    fun setSpawnDetails(details: MutableList<SpawnDetail>)

    fun modifySpawn(entity: Entity) {}
    fun afterSpawn(entity: Entity) {}
    fun canSpawn(): Boolean
    fun getMatchingSpawns(ctx: SpawningContext) = ((getPrecalculationResult()?.retrieve(ctx)) ?: getSpawnDetails()).filter { it.isSatisfiedBy(ctx) }

    fun registerPrecalculator(precalculation: SpawningPrecalculation<*>) {
        precalculators.add(precalculation)
    }

    fun precalculate() {
        if (precalculators.isEmpty()) {
            setPrecalculationResult(
                FinalPrecalculationResult<Any>(
                    calculation = RootPrecalculation,
                    mapping = mutableMapOf(Unit to getSpawnDetails())
                )
            )
        }

        setPrecalculationResult(precalculators.first().generate(getSpawnDetails(), precalculators.subList(1, precalculators.size)))
    }

    fun copyInfluences() = influences.toMutableList()
}