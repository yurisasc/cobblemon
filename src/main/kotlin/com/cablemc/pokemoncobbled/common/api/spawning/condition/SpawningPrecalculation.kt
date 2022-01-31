package com.cablemc.pokemoncobbled.common.api.spawning.condition

import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import com.cablemc.pokemoncobbled.common.api.spawning.spawner.Spawner

/**
 * A type of precalculation that can occur on a list of [SpawnDetail] to accelerate
 * checks on whether a spawn can occur at a particular [SpawningContext]. This process
 * works by creating a [HashMap] keying from some property of a [SpawnDetail] and
 * [SpawningContext] and mapping it to a list of spawns that were calculated ahead of
 * time as sharing that value. The requirement of implementations is only to get that
 * key from both a [SpawnDetail] and a [SpawningContext].
 *
 * When a precalculation has been registered with a [Spawner] by adding to [Spawner.precalculators],
 * the precalculation result will need to be updated by running [Spawner.precalculate]. That
 * might take it a hot minute, but the result will be that there is an extra layer of
 * shortcutting to the matching algorithm.
 *
 * @author Hiroku
 * @since January 31st, 2022
 */
interface SpawningPrecalculation<T : Any> {
    /** Retrieves the precalculation key for the given spawn detail, if that key exists for this detail. */
    fun select(detail: SpawnDetail): T?
    /** Retrieves the precalculation key for the given context, if that key exists for this context. */
    fun select(ctx: SpawningContext): T?

    /**
     * Generates a precalculation result for the list of [SpawnDetail]s and the subsequent precalculations.
     *
     * This either returns a [NestedPrecalculationResult] (when there are some later precalculations to do)
     * or a [FinalPrecalculationResult] if there are no further precalculations to do.
     *
     * This function is vaguely recursive but unless the function itself is bugged then it won't overflow.
     */
    fun generate(details: List<SpawnDetail>, next: List<SpawningPrecalculation<*>>): PrecalculationResult<T> {
        val mapping = details
            .filter { select(it) != null }
            .groupBy { select(it)!! }

        if (next.isEmpty()) {
            return FinalPrecalculationResult(calculation = this, mapping = mapping)
        } else {
            val immediateNext = next.first()
            val subNext = next.subList(1, next.size)
            return NestedPrecalculationResult(
                calculation = this,
                mapping = mapping.entries.associate { it.key to immediateNext.generate(it.value, subNext) }
            )
        }
    }
}

/**
 * A dummy precalculation that doesn't actually do any precalculating. Used for situations
 * where there weren't any precalculators registered.
 *
 * @author Hiroku
 * @since January 31st, 2022
 */
object RootPrecalculation : SpawningPrecalculation<Any> {
    override fun select(detail: SpawnDetail): Any = Unit
    override fun select(ctx: SpawningContext): Any = Unit
}

/**
 * The result of a precalculation. This is mostly just a delegate so that the next
 * result of a precalculation can either be an actual list of spawns or merely another
 * layer of precalculation.
 *
 * @author Hiroku
 * @since January 31st, 2022
 */
sealed class PrecalculationResult<T : Any>(
    val calculation: SpawningPrecalculation<*>
) {
    /** Gets the list of spawn details that fit this context. */
    abstract fun retrieve(ctx: SpawningContext): List<SpawnDetail>
}

/**
 * A precalculation result when there was another precalculation made on the result.
 *
 * @author Hiroku
 * @since January 31st, 2022
 */
class NestedPrecalculationResult<T : Any>(
    calculation: SpawningPrecalculation<T>,
    val mapping: Map<T, PrecalculationResult<*>> = mutableMapOf()
) : PrecalculationResult<T>(calculation) {
    override fun retrieve(ctx: SpawningContext) = mapping[calculation.select(ctx)]?.retrieve(ctx) ?: emptyList()
}

/**
 * The final stage of a precalculation where there is a final mapping from the key
 * to a list of spawns. Every precalculation tree ends in this type of result.
 *
 * @author Hiroku
 * @since January 31st, 2022
 */
class FinalPrecalculationResult<T : Any>(
    calculation: SpawningPrecalculation<*>,
    val mapping: Map<T, List<SpawnDetail>>
) : PrecalculationResult<T>(calculation) {
    override fun retrieve(ctx: SpawningContext) = mapping[calculation.select(ctx)] ?: emptyList()
}