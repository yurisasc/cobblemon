package com.cobblemon.mod.common.api.spawning.fallingstar

import com.cobblemon.mod.common.api.spawning.CobblemonSpawnPools
import com.cobblemon.mod.common.api.spawning.context.FallingStarSpawningContext
import com.cobblemon.mod.common.api.spawning.detail.SpawnPool
import com.cobblemon.mod.common.api.spawning.spawner.TriggerSpawner
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

open class FallingStarSpawner(
    override val name: String = "fallingstar",
    pool: SpawnPool = CobblemonSpawnPools.WORLD_SPAWN_POOL
) : TriggerSpawner<FallingStarSpawnCause>(name, pool) {
    override fun parseContext(cause: FallingStarSpawnCause, world: ServerWorld, pos: BlockPos): FallingStarSpawningContext? {
        return FallingStarSpawningContext(cause, world, pos, influences)
    }
}