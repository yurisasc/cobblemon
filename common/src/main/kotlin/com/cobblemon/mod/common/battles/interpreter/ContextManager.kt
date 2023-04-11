package com.cobblemon.mod.common.battles.interpreter

import com.cobblemon.mod.common.api.battles.interpreter.BattleContext

/**
 * Maintains buckets of [BattleContext] during a battle.
 *
 * @author Segfault Guy
 * @since April 10th, 2023
 */
class ContextManager {

    /** BattleContext.Type buckets containing BattleContexts of that type. */
    val buckets = hashMapOf<BattleContext.Type, MutableCollection<BattleContext>>()

    /** Adds a [BattleContext] to its corresponding [BattleContext.Type] bucket. */
    fun add(context: BattleContext) {
        if (context.type.exclusive) {
            val bucket = this.buckets.getOrPut(context.type) { mutableListOf() }
            bucket.clear()
            bucket.add(context)
        }
        else {
            this.buckets.getOrPut(context.type) { mutableListOf() }.add(context)
        }
    }

    /** Adds a [BattleContext] to its corresponding [BattleContext.Type] bucket, if the bucket does not have a context
     * of the same id. */
    fun addUnique(context: BattleContext) {
        if (this.buckets[context.type]?.find { it.id == context.id } == null) {
            add(context)
        }
    }

    /** Removes all [BattleContext]s of the [BattleContext.Type] bucket that match the contextID. */
    fun remove(contextID: String, bucketType: BattleContext.Type) {
        if (bucketType.exclusive) {
            this.buckets[bucketType]?.clear()
        }
        else {
            this.buckets[bucketType]?.removeIf{it.id == contextID}
        }
    }

    /** Clears all the [BattleContext]s belonging to the [BattleContext.Type] bucket. */
    fun clear(bucketType: BattleContext.Type) {
        this.buckets[bucketType]?.clear()
    }

    companion object {

        /**
         * Extracts a [BattleContext] from multiple context buckets.
         *
         * @return the most recently added context with a matching id.
         * */
        fun scoop(contextID: String, vararg contextBuckets: Collection<BattleContext>?): BattleContext? {
            contextBuckets.filterNotNull().forEach { bucket ->
                bucket.findLast { it.id == contextID }?.let { return it }
            }
            return null
        }
    }
}