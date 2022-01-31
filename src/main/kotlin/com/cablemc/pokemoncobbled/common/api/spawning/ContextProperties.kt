package com.cablemc.pokemoncobbled.common.api.spawning

import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.detail.RegisteredSpawnDetail
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail

/**
 * Properties to apply to a specific [SpawningContext] that overwrite the root
 * [SpawnDetail]. This is for cases such as where the rarity of a spawn is
 * different depending on what sort of place it's spawning in. This is abstract
 * quite crucially because it can be made to replace many details about a
 * [SpawnDetail] implementation.
 *
 * These are registered alongside a [SpawnDetail] with a [RegisteredSpawnDetail]
 * which means that for a single [SpawnDetail], all of the context properties
 * will be of the same implementation class automatically.
 *
 * @author Hiroku
 * @since January 31st, 2022
 */
abstract class ContextProperties(val detail: SpawnDetail) {
    var rarity: Float? = null
        get() = field ?: detail.rarity
    var percentage: Float? = null
        get() = field ?: detail.percentage
}