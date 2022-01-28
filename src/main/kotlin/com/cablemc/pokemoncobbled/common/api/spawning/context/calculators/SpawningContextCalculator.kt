package com.cablemc.pokemoncobbled.common.api.spawning.context.calculators

import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level

interface SpawningContextCalculator<I : SpawningContextInput, O : SpawningContext> {
    fun calculate(input: I): O
}

open class SpawningContextInput(
    /** What caused the spawn context. Almost always will be a player entity. */
    val cause: Any,
    /** The [Level] the spawning context exists in. */
    val level: Level
)