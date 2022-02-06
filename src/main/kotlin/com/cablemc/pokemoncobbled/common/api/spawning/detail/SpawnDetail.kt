package com.cablemc.pokemoncobbled.common.api.spawning.detail

import com.cablemc.pokemoncobbled.common.api.spawning.ContextProperties
import com.cablemc.pokemoncobbled.common.api.spawning.ContextPropertyMap
import com.cablemc.pokemoncobbled.common.api.spawning.condition.CompositeSpawningCondition
import com.cablemc.pokemoncobbled.common.api.spawning.condition.SpawningCondition
import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.spawner.Spawner

/**
 * A spawnable unit in the Best Spawner API. This is extended for any kind of entity
 * you want to spawn.
 *
 * @author Hiroku
 * @since January 31st, 2022
 */
abstract class SpawnDetail {
    companion object {
        val spawnDetailTypes = mutableMapOf<String, RegisteredSpawnDetail<*, *>>()
        fun <T : SpawnDetail, U : ContextProperties> registerSpawnType(name: String, detailClass: Class<T>, contextualPropertiesClass: Class<U>) {
            spawnDetailTypes[name] = RegisteredSpawnDetail(detailClass, contextualPropertiesClass)
        }
    }

    abstract val type: String
    var id = ""
    var conditions = mutableListOf<SpawningCondition<*>>()
    var anticonditions = mutableListOf<SpawningCondition<*>>()
    var compositeCondition: CompositeSpawningCondition? = null

    var rarity = -1F
    var percentage = -1F

    var labels = mutableListOf<String>()
    var contextProperties: ContextPropertyMap? = null

    fun getContextProperties(ctx: SpawningContext): ContextProperties? {
        return contextProperties?.entries?.find { it.key.clazz.isAssignableFrom(ctx::class.java) }?.value
    }

    // TODO consider precalculating the possible contexts

    open fun autoLabel() {}

    open fun isSatisfiedBy(ctx: SpawningContext): Boolean {
        if (!ctx.preFilter(this)) {
            return false
        } else if (conditions.isNotEmpty() && conditions.none { it.isSatisfiedBy(ctx, this) }) {
            return false
        } else if (anticonditions.isNotEmpty() && anticonditions.any { it.isSatisfiedBy(ctx, this) }) {
            return false
        } else if (compositeCondition?.satisfiedBy(ctx, this) == false) {
            return false
        }
        return true
    }

    abstract fun doSpawn(spawner: Spawner, ctx: SpawningContext): SpawnAction<*>
}