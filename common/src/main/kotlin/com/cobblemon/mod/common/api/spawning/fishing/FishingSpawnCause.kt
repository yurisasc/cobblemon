/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.spawning.fishing

import com.cobblemon.mod.common.api.spawning.SpawnBucket
import com.cobblemon.mod.common.api.spawning.SpawnCause
import com.cobblemon.mod.common.api.spawning.spawner.Spawner
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.item.interactive.PokerodItem
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack

/**
 * A spawning cause that is embellished with fishing information. Could probably also
 * have the bobber entity or something.
 *
 * @author Hiroku
 * @since February 3rd, 2024
 */
class FishingSpawnCause(
    spawner: Spawner,
    bucket: SpawnBucket,
    entity: Entity?,
    val rodStack: ItemStack
) : SpawnCause(spawner, bucket, entity) {
    companion object {
        const val FISHED_ASPECT = "fished"
    }

    val rodItem = rodStack.item as? PokerodItem

    override fun affectSpawn(entity: Entity) {
        super.affectSpawn(entity)
        if (entity is PokemonEntity) {
            entity.pokemon.forcedAspects += FISHED_ASPECT
        }
    }
}