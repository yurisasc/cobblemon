/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api.spawning.influence

import com.cablemc.pokemoncobbled.common.api.spawning.BestSpawner
import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnAction
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import net.minecraft.entity.Entity

/**
 * An influence over spawning that can affect various parts of the spawning process. These can be attached to
 * various areas of the [BestSpawner] for either momentary or extended effects.
 *
 * @author Hiroku
 * @since January 31st, 2022
 */
interface SpawningInfluence {
    /** Whether this influence has passed and should be removed. */
    fun isExpired(): Boolean = false
    /** Returns true if the given spawn detail is able to spawn under this influence. */
    fun affectSpawnable(detail: SpawnDetail, ctx: SpawningContext): Boolean = true
    /** Returns the effective weight of spawning under this influence. This is after typical weight multipliers. */
    fun affectWeight(detail: SpawnDetail, ctx: SpawningContext, weight: Float): Float = weight
    /** Affects the spawn action prior to it generating the entity. */
    fun affectAction(action: SpawnAction<*>) {}
    /** Applies some influence over the entity that's been spawned. */
    fun affectSpawn(entity: Entity) {}
}