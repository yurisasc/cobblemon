package com.cobblemon.mod.common.api.spawning.condition

import com.cobblemon.mod.common.api.spawning.context.FallingStarSpawningContext

class FallingStartSpawningCondition: SpawningCondition<FallingStarSpawningContext>() {
    override fun contextClass() = FallingStarSpawningContext::class.java

    override fun fits(ctx: FallingStarSpawningContext): Boolean {
        return true
    }

    companion object {
        const val NAME = "fallingstar"
    }
}