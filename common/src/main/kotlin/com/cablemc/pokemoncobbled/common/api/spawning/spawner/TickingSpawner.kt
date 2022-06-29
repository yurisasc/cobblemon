package com.cablemc.pokemoncobbled.common.api.spawning.spawner

import com.cablemc.pokemoncobbled.common.api.spawning.SpawnCause
import com.cablemc.pokemoncobbled.common.api.spawning.SpawnerManager
import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnAction
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnPool
import com.cablemc.pokemoncobbled.common.api.spawning.influence.SpawningInfluence
import com.cablemc.pokemoncobbled.common.api.spawning.selection.ContextWeightedSelector
import com.cablemc.pokemoncobbled.common.api.spawning.selection.SpawningSelector
import net.minecraft.entity.Entity

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
    var spawns: SpawnPool,
    val manager: SpawnerManager
) : Spawner {
    private var selector: SpawningSelector = ContextWeightedSelector
    override val influences = mutableListOf<SpawningInfluence>()

    override fun canSpawn() = active
    override fun getSpawningSelector() = selector
    override fun setSpawningSelector(selector: SpawningSelector) { this.selector = selector }
    override fun getSpawnPool() = spawns
    override fun setSpawnPool(spawnPool: SpawnPool) { spawns = spawnPool }

    abstract fun run(cause: SpawnCause): Pair<SpawningContext, SpawnDetail>?

    var active = true
    val spawnedEntities = mutableListOf<Entity>()
    var maximumSpawned = 15

    var lastSpawnTime = 0L
    var ticksUntilNextSpawn = 100F
    var ticksBetweenSpawns = 60F
    var tickTimerMultiplier = 1F

    @Volatile
    var scheduledSpawn: SpawnAction<*>? = null

    var removalCheckTicks = 0

    open fun tick() {
        removalCheckTicks++
        if (removalCheckTicks == 60) {
            spawnedEntities.removeIf { it.isRemoved }
            removalCheckTicks = 0
        }

        if (!active || spawnedEntities.size >= maximumSpawned) {
            return
        }

        val scheduledSpawn = scheduledSpawn
        if (scheduledSpawn != null) {
            performSpawn(scheduledSpawn)
        }

        ticksUntilNextSpawn -= tickTimerMultiplier
        if (ticksUntilNextSpawn <= 0) {
            // TODO some kind of async logic would be nice.
            val spawn = run(SpawnCause(spawner = this, bucket = chooseBucket(), entity = getCauseEntity()))
            ticksUntilNextSpawn = ticksBetweenSpawns
            if (spawn != null) {
                val ctx = spawn.first
                val detail = spawn.second
                val spawnAction = detail.doSpawn(ctx = ctx)
                this.scheduledSpawn = spawnAction
            }
        }
    }

    override fun afterSpawn(entity: Entity) {
        super.afterSpawn(entity)
        spawnedEntities.add(entity)
        lastSpawnTime = System.currentTimeMillis()
    }

    fun performSpawn(spawnAction: SpawnAction<*>) {
        spawnAction.entity.subscribe { afterSpawn(it) }
        spawnAction.run()
        this.scheduledSpawn = null
    }

    open fun getCauseEntity(): Entity? = null
}