package com.cablemc.pokemoncobbled.common.api.spawning.context

import com.cablemc.pokemoncobbled.common.util.toBlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.phys.Vec3

/**
 * A context upon which spawning is being attempted. This supplies all the information that can be used to asynchronously
 * check for suitability of spawning.
 *
 * @author Hiroku
 * @since January 24th, 2022
 */
abstract class SpawningContext {
    /** What caused the spawn context. Almost always will be a player entity. */
    abstract val cause: Any
    /** What [Level] the spawning context exists. */
    abstract val level: Level
    /** The exact vector location of the spawning attempt. */
    abstract val position: Vec3
    /** The light level at this location. */
    abstract val light: Float
    /** The current phase of the moon at this location. */
    val moonPhase: Int by lazy { level.moonPhase }
    /** The biome of this location. */
    val biome: Biome by lazy { level.getBiome(position.toBlockPos()) }
}