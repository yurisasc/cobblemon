/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api.spawning.spawner

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.spawning.BestSpawner
import com.cablemc.pokemoncobbled.common.api.spawning.SpawnBucket
import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnPool
import com.cablemc.pokemoncobbled.common.api.spawning.influence.SpawningInfluence
import com.cablemc.pokemoncobbled.common.api.spawning.selection.SpawningSelector
import java.util.Random
import net.minecraft.entity.Entity

/**
 * Interface representing something that performs the action of spawning. Various functions
 * exist to streamline the process of using the [BestSpawner].
 *
 * @author Hiroku
 * @since January 24th, 2022
 */
interface Spawner {
    companion object {
        // var worker = Executors.newSingleThreadExecutor { r -> Thread(r, "Spawning Worker") }
    }

    val name: String
    val influences: MutableList<SpawningInfluence>
    fun getSpawningSelector(): SpawningSelector
    fun setSpawningSelector(selector: SpawningSelector)
    fun getSpawnPool(): SpawnPool
    fun setSpawnPool(spawnPool: SpawnPool)
    fun modifySpawn(entity: Entity) {}
    fun afterSpawn(entity: Entity) {}
    fun canSpawn(): Boolean
    fun getMatchingSpawns(ctx: SpawningContext) = getSpawnPool().retrieve(ctx).filter { it.isSatisfiedBy(ctx) }
    fun copyInfluences() = influences.filter { !it.isExpired() }.toMutableList()
    fun chooseBucket(): SpawnBucket {
        val buckets = PokemonCobbled.bestSpawner.config.buckets
        val weightSum = buckets.sumOf { it.weight.toDouble() }.toFloat()
        // Make the 0 exclusive and the weightSum inclusive on the random
        val chosenSum = weightSum - Random().nextFloat(weightSum)
        var sum = 0F
        for (bucket in buckets) {
            sum += bucket.weight
            if (sum >= chosenSum) {
                return bucket
            }
        }
        return buckets.first()
    }
}