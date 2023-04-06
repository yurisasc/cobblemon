package com.cobblemon.mod.common.api.battles.interpreter

import com.cobblemon.mod.common.battles.pokemon.BattlePokemon

// Organizes the history of changes and effects that Pokemon have on the battle
interface BattleContext {

    val id: String
    val turn: Int
    val origin: BattlePokemon?

    companion object {
        fun scoop(effectId: String, vararg contextBuckets: Collection<BattleContext>?): BattleContext? {
            contextBuckets.filterNotNull().forEach {
                for (context in it) {
                    if (context.id == effectId) return context
                }
            }
            return null
        }

        fun add(context: BattleContext, bucketType: Context, contextBuckets: HashMap<Context, MutableCollection<BattleContext>> ) {
            if (exclusiveContexts.contains(bucketType)) {
                val bucket = contextBuckets.getOrPut(bucketType) { mutableListOf() }
                bucket.clear()
                bucket.add(context)
            }
            else {
                contextBuckets.getOrPut(bucketType) { mutableListOf() }.add(context)
            }
        }

        fun remove(contextID: String?, bucketType: Context, contextBuckets: HashMap<Context, MutableCollection<BattleContext>>) {
            if (contextID == null && exclusiveContexts.contains(bucketType)) {
                contextBuckets.remove(bucketType)
            }
            else {
                contextBuckets[bucketType]?.removeIf{it.id == contextID}
            }
        }

        val damageContexts = mutableListOf(Context.ITEM, Context.STATUS, Context.VOLATILE, Context.HAZARD, Context.WEATHER)
        val exclusiveContexts = mutableListOf(Context.WEATHER, Context.TERRAIN, Context.ROOM, Context.GRAVITY, Context.TAILWIND,
                Context.STATUS, Context.ITEM)
    }
}

data class BasicContext(
        override val id: String,
        override val turn: Int,
        override val origin: BattlePokemon?
) : BattleContext

data class MissingContext(
        override val id: String = "error",
        override val turn: Int = 0,
        override val origin: BattlePokemon? = null
) : BattleContext

data class HazardContext(
        override val id: String,
        override val turn: Int,
        override val origin: BattlePokemon,
        var stacks: Int = 0
) : BattleContext

enum class Context() {
    ITEM,
    STATUS,
    VOLATILE,
    HAZARD,
    WEATHER,
    ROOM,
    SPORT,
    TERRAIN,
    GRAVITY,
    TAILWIND,
    SCREEN,
    FAINT,
    MISC;
}
