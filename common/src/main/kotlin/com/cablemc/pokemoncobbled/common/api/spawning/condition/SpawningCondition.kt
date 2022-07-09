package com.cablemc.pokemoncobbled.common.api.spawning.condition

import com.cablemc.pokemoncobbled.common.api.spawning.BiomeLikeCondition
import com.cablemc.pokemoncobbled.common.api.spawning.condition.ListCheckMode.ALL
import com.cablemc.pokemoncobbled.common.api.spawning.condition.ListCheckMode.ANY
import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import com.cablemc.pokemoncobbled.common.util.Merger
import com.cablemc.pokemoncobbled.common.util.math.orMax
import com.cablemc.pokemoncobbled.common.util.math.orMin
import net.minecraft.util.Identifier

/**
 * The root of spawning conditions that can be applied to a spawning context. What type
 * of spawning context it can be applied to is relevant for any subclasses.
 *
 * @author Hiroku
 * @since January 24th, 2022
 */
abstract class SpawningCondition<T : SpawningContext> {
    companion object {
        val conditionTypes = mutableMapOf<String, Class<out SpawningCondition<*>>>()
        fun getByName(name: String) = conditionTypes[name]
        fun <T : SpawningContext, C : SpawningCondition<T>> register(name: String, clazz: Class<C>) {
            conditionTypes[name] = clazz
        }
    }

    var dimensions: MutableList<Identifier>? = null
    var biomes: MutableSet<BiomeLikeCondition<*>>? = null
    var moonPhase: Int? = null
    var canSeeSky: Boolean? = null
    var minX: Float? = null
    var minY: Float? = null
    var minZ: Float? = null
    var maxX: Float? = null
    var maxY: Float? = null
    var maxZ: Float? = null
    var minLight: Int? = null
    var maxLight: Int? = null
    var isRaining: Boolean? = null
    var isThundering: Boolean? = null
    var timeRange: TimeRange? = null
    var labels: MutableList<String>? = null
    var labelMode = ANY

    @Transient
    var appendages = mutableListOf<AppendageCondition>()

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
        } else if (dimensions != null && dimensions!!.isNotEmpty() && ctx.world.dimension.effects !in dimensions!!) {
            return false
        } else if (moonPhase != null && moonPhase != ctx.moonPhase) {
            return false
        } else if (biomes != null && biomes!!.isNotEmpty() && biomes!!.none { condition -> condition.accepts(ctx.biome, ctx.biomeRegistry) }) {
            return false
        } else if (ctx.light > maxLight.orMax() || ctx.light < minLight.orMin()) {
            return false
        } else if (timeRange != null && !timeRange!!.contains((ctx.world.timeOfDay % 24000).toInt())) {
            return false
        } else if (canSeeSky != null && canSeeSky != ctx.canSeeSky) {
            return false
        } else if (isRaining != null && ctx.world.isRaining != isRaining!!) {
            return false
        } else if (isThundering != null && ctx.world.isThundering != isThundering!!) {
            return false
        } else if (labels != null && labels!!.isNotEmpty() &&
            (
                (labelMode == ANY && labels!!.none { it in detail.labels }) ||
                (labelMode == ALL && labels!!.any { it !in detail.labels })
            )
        ) {
            return false
        } else if (appendages.any { !it.fits(ctx, detail) }) {
            return false
        }

        return true
    }

    open fun copyFrom(other: SpawningCondition<*>, merger: Merger) {
        dimensions = merger.merge(dimensions, other.dimensions)?.toMutableList()
        biomes = merger.merge(biomes, other.biomes)?.toMutableSet()
        labels = merger.merge(labels, other.labels)?.toMutableList()
        if (other.moonPhase != null) moonPhase = other.moonPhase
        if (other.skyAbove != null) skyAbove = other.skyAbove
        if (other.minX != null) minX = other.minX
        if (other.minY != null) minY = other.minY
        if (other.minZ != null) minZ = other.minZ
        if (other.maxX != null) maxX = other.maxX
        if (other.maxY != null) maxY = other.maxY
        if (other.maxZ != null) maxZ = other.maxZ
        if (other.minLight != null) minLight = other.minLight
        if (other.maxLight != null) maxLight = other.maxLight
        if (other.timeRange != null) timeRange = other.timeRange
        if (other.labelMode != ANY) labelMode = other.labelMode
    }
}