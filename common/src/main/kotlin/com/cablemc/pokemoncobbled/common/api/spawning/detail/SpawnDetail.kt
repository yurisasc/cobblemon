package com.cablemc.pokemoncobbled.common.api.spawning.detail

import com.cablemc.pokemoncobbled.common.api.ModDependant
import com.cablemc.pokemoncobbled.common.api.spawning.SpawnBucket
import com.cablemc.pokemoncobbled.common.api.spawning.condition.CompositeSpawningCondition
import com.cablemc.pokemoncobbled.common.api.spawning.condition.SpawningCondition
import com.cablemc.pokemoncobbled.common.api.spawning.context.RegisteredSpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.multiplier.WeightMultiplier
import com.cablemc.pokemoncobbled.common.api.text.text
import com.cablemc.pokemoncobbled.common.util.asTranslated
import net.minecraft.text.MutableText

/**
 * A spawnable unit in the Best Spawner API. This is extended for any kind of entity
 * you want to spawn.
 *
 * @author Hiroku
 * @since January 31st, 2022
 */
abstract class SpawnDetail : ModDependant {
    companion object {
        val spawnDetailTypes = mutableMapOf<String, RegisteredSpawnDetail<*>>()
        fun <T : SpawnDetail> registerSpawnType(name: String, detailClass: Class<T>) {
            spawnDetailTypes[name] = RegisteredSpawnDetail(detailClass)
        }
    }

    abstract val type: String
    var id = ""
    var displayName: String? =  null
    lateinit var context: RegisteredSpawningContext<*>
    var bucket = SpawnBucket("", 0F)
    var conditions = mutableListOf<SpawningCondition<*>>()
    var anticonditions = mutableListOf<SpawningCondition<*>>()
    var compositeCondition: CompositeSpawningCondition? = null
    var weightMultipliers = mutableListOf<WeightMultiplier>()

    var weight = -1F
    var percentage = -1F

    var labels = mutableListOf<String>()

    override var neededInstalledMods = listOf<String>()
    override var neededUninstalledMods = listOf<String>()

    open fun autoLabel() {}
    open fun getName() = displayName?.asTranslated() ?: id.text()

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

    abstract fun doSpawn(ctx: SpawningContext): SpawnAction<*>
}