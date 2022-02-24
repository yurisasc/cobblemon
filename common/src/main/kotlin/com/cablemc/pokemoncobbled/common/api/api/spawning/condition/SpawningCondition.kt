package com.cablemc.pokemoncobbled.common.api.spawning.condition

import com.cablemc.pokemoncobbled.common.api.spawning.BiomeList
import com.cablemc.pokemoncobbled.common.api.spawning.condition.ListCheckMode.ALL
import com.cablemc.pokemoncobbled.common.api.spawning.condition.ListCheckMode.ANY
import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import com.cablemc.pokemoncobbled.common.util.math.orMax
import com.cablemc.pokemoncobbled.common.util.math.orMin
import net.minecraft.resources.ResourceLocation

/**
 * The root of spawning conditions that can be applied to a spawning context. What type
 * of spawning context it can be applied to is relevant for any subclasses.
 *
 * @author Hiroku
 * @since January 24th, 2022
 */
abstract class SpawningCondition<T : SpawningContext> {
    companion object {
        private val conditionTypes = mutableMapOf<String, Class<out SpawningCondition<*>>>()
        fun getByName(name: String) = conditionTypes[name]
        fun <T : SpawningContext, C : SpawningCondition<T>> register(name: String, clazz: Class<C>) {
            conditionTypes[name] = clazz
        }
    }

    val dimensions: MutableList<ResourceLocation> = mutableListOf()
    val biomes = BiomeList()
    val moonPhase: Int? = null
    var skyAbove: Boolean? = null
    var minX: Float? = null
    var minY: Float? = null
    var minZ: Float? = null
    var maxX: Float? = null
    var maxY: Float? = null
    var maxZ: Float? = null
    var minLight: Int? = null
    var maxLight: Int? = null
    var timeRange: TimeRange? = null
    var labels: List<String>? = null
    var labelMode = ANY

    abstract fun contextClass(): Class<out T>
    fun contextMatches(ctx: SpawningContext) = contextClass().isAssignableFrom(ctx::class.java)

    fun isSatisfiedBy(ctx: SpawningContext, detail: SpawnDetail): Boolean {
        return if (contextMatches(ctx)) {
            fits(ctx as T, detail)
        } else {
            false
        }
    }

    protected open fun fits(ctx: T, detail: SpawnDetail): Boolean {
        if (ctx.position.x < minX.orMin() || ctx.position.x > maxX.orMax()) {
            return false
        } else if (ctx.position.y < minY.orMin() || ctx.position.y > maxY.orMax()) {
            return false
        } else if (ctx.position.z < minZ.orMin() || ctx.position.z > maxZ.orMax()) {
            return false
        } else if (dimensions.isNotEmpty() && ctx.level.dimension().registryName !in dimensions) {
            return false
        } else if (moonPhase != null && moonPhase != ctx.moonPhase) {
            return false
        } else if (biomes.isNotEmpty() && biomes.none { it != ctx.biome }) {
            return false
        } else if (ctx.light > maxLight.orMax() || ctx.light < minLight.orMin()) {
            return false
        } else if (timeRange != null && !timeRange!!.contains((ctx.level.dayTime() % 24000).toInt())) {
            return false
        } else if (skyAbove != null && skyAbove != ctx.skyAbove) {
            return false
        } else if (labels != null && labels!!.isNotEmpty() &&
            (
                (labelMode == ANY && labels!!.none { it in detail.labels }) ||
                (labelMode == ALL && labels!!.any { it !in detail.labels })
            )
        ) {
            return false
        }

        return true
    }
}