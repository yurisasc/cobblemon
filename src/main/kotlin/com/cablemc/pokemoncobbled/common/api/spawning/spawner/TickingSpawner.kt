package com.cablemc.pokemoncobbled.common.api.spawning.spawner

import com.cablemc.pokemoncobbled.common.api.spawning.SpawnerManager
import com.cablemc.pokemoncobbled.common.api.spawning.condition.PrecalculationResult
import com.cablemc.pokemoncobbled.common.api.spawning.condition.RootPrecalculation
import com.cablemc.pokemoncobbled.common.api.spawning.condition.SpawningPrecalculation
import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnAction
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import com.cablemc.pokemoncobbled.common.api.spawning.influence.SpawningInfluence
import com.cablemc.pokemoncobbled.common.api.spawning.selection.ContextWeightedSelector
import com.cablemc.pokemoncobbled.common.api.spawning.selection.SpawningSelector
import net.minecraft.world.entity.Entity
import java.util.UUID

/**
 * A spawner that regularly attempts spawning entities. It has timing utilities,
 * and subclasses must provide the logic for generating a spawn which is called
 * periodically by the server.
 *
 * @author Hiroku
 * @since February 5th, 2022
 */
abstract class TickingSpawner(
    override val name: String,
    private var details: MutableList<SpawnDetail> = mutableListOf(),
    val manager: SpawnerManager
) : Spawner {
    private var selector: SpawningSelector = ContextWeightedSelector
    private var precalculation: PrecalculationResult<*> = RootPrecalculation.generate(details, emptyList())

    override val precalculators = mutableListOf<SpawningPrecalculation<*>>()
    override val influences = mutableListOf<SpawningInfluence>()

    override fun canSpawn() = active
    override fun getPrecalculationResult() = precalculation
    override fun setPrecalculationResult(precalculation: PrecalculationResult<*>) { this.precalculation = precalculation }
    override fun getSpawningSelector() = selector
    override fun setSpawningSelector(selector: SpawningSelector) { this.selector = selector }
    override fun getSpawnDetails() = details
    override fun setSpawnDetails(details: MutableList<SpawnDetail>) {
        this.details = details
        precalculate()
    }

    abstract fun run(): Pair<SpawningContext, SpawnDetail>?

    var active = true
    val spawnedEntities = mutableListOf<UUID>()

    var lastSpawnTime = 0L
    var ticksUntilNextSpawn = 100F
    var ticksBetweenSpawns = 120F
    var tickTimerMultiplier = 1F

    @Volatile
    var scheduledSpawn: SpawnAction<*>? = null

    open fun tick() {
        if (!active) {
            return
        }

        val scheduledSpawn = scheduledSpawn
        if (scheduledSpawn != null) {
            performSpawn(scheduledSpawn)
        }

        ticksUntilNextSpawn -= tickTimerMultiplier
        if (ticksUntilNextSpawn <= 0) {
            // TODO some kind of async logic would be nice.
            val spawn = run()
            ticksUntilNextSpawn = ticksBetweenSpawns
            if (spawn != null) {
                val ctx = spawn.first
                val detail = spawn.second
                val spawnAction = detail.doSpawn(spawner = this, ctx = ctx)
                this.scheduledSpawn = spawnAction
            }
        }
    }

    override fun afterSpawn(entity: Entity) {
        super.afterSpawn(entity)
        spawnedEntities.add(entity.uuid)
        lastSpawnTime = System.currentTimeMillis()
    }

    fun performSpawn(spawnAction: SpawnAction<*>) {
        spawnAction.run()
        this.scheduledSpawn = null
        spawnAction.entity.subscribe { afterSpawn(it) }
    }
}