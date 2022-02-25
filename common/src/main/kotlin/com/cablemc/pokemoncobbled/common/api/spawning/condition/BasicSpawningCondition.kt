package com.cablemc.pokemoncobbled.common.api.spawning.condition

import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext

/**
 * A basic spawning condition that works for any type of spawning context.
 *
 * @author Hiroku
 * @since February 7th, 2022
 */
class BasicSpawningCondition : SpawningCondition<SpawningContext>() {
    override fun contextClass(): Class<out SpawningContext> = SpawningContext::class.java
    companion object {
        const val NAME = "basic"
    }
}