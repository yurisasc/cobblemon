package com.cablemc.pokemoncobbled.common.api.spawning.selection

import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import com.cablemc.pokemoncobbled.common.api.spawning.selection.ContextWeightedSelector.getWeight
import com.cablemc.pokemoncobbled.common.api.spawning.spawner.Spawner
import com.cablemc.pokemoncobbled.common.util.weightedSelection
import com.cablemc.pokemoncobbled.mod.PokemonCobbledMod
import kotlin.math.max
import kotlin.random.Random

/**
 * A spawning selector that randomly selects from each spawning context, and then
 * chooses which context will spawn an entity by weighted selecting what type of
 * context will spawn, and then un-weighted selecting which context of that type
 * will spawn. The weights applied to each spawning context is determined by
 * the [getWeight] function.
 *
 * The goal of this algorithm is to prevent a high density of a specific spawning
 * context from unfairly crowding a spawning pool. If there are many places that
 * suit air spawns, and just as many that support land spawns, that would create
 * a situation where both air and land spawns occur with the same frequency. The
 * goal of this algorithm allows, for example, the air context to be weighted very
 * lightly so that it's less likely, while still allowing the distribution of spawns
 * at specific contexts to be respected.
 *
 * @author Hiroku
 * @since February 5th, 2022
 */
object ContextWeightedSelector : SpawningSelector {
    fun getWeight(clazz: Class<out SpawningContext>): Float {
        // TODO proper variables for the context weights
        return 1F
    }

    override fun select(
        spawner: Spawner,
        contexts: List<SpawningContext>,
        spawnDetails: List<SpawnDetail>
    ): Pair<SpawningContext, SpawnDetail>? {
        val contextToSpawn = mutableMapOf<SpawningContext, SpawnDetail>()
        val contextTypes = mutableListOf<Class<out SpawningContext>>()

        contexts.forEach { ctx ->
            val possible = spawner.getMatchingSpawns(ctx)

            // Prior to weighted selection, % check
            var percentSum = possible.sumOf { max(it.percentage, 0F).toDouble() }.toFloat()
            if (percentSum > 100) {
                PokemonCobbledMod.LOGGER.warn(
                    """
                        A spawn list for ${spawner.name} exceeded 100% on percentage sums... 
                        This means you don't understand how this option works.
                    """.trimIndent()
                )
            } else {
                val chosenPercent = Random.Default.nextFloat() * 100F
                if (chosenPercent <= percentSum) {
                    percentSum = 0F
                    for (spawn in possible) {
                        if (spawn.percentage > 0) {
                            percentSum += spawn.percentage
                            if (percentSum >= chosenPercent) {
                                contextToSpawn[ctx] = spawn
                                return@forEach
                            }
                        }
                    }
                }
            }

            val possibleToWeight = possible.associateWith { ctx.getRarity(it) }

            possibleToWeight.entries.weightedSelection { it.value }?.let {
                contextToSpawn[ctx] = it.key
                val clazz = ctx::class.java
                if (clazz !in contextTypes) {
                    contextTypes.add(clazz)
                }
            }
        }

        if (contextToSpawn.isEmpty()) {
            return null
        }

        val contextType = contextTypes.weightedSelection { getWeight(it) } ?: return null
        val chosenContext = contextToSpawn.keys.filterIsInstance(contextType).random()
        return chosenContext to contextToSpawn[chosenContext]!!
    }

    override fun getProbabilities(
        spawner: Spawner,
        contexts: List<SpawningContext>,
        spawnDetails: List<SpawnDetail>
    ): Map<String, Float> {
        TODO("Checkspawns calculations in Context Weighted Selector")
    }
}