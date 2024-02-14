package com.cobblemon.mod.common.api.spawning.context

import com.cobblemon.mod.common.api.spawning.fallingstar.FallingStarSpawnCause
import com.cobblemon.mod.common.api.spawning.influence.SpawningInfluence
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

class FallingStarSpawningContext(
    cause: FallingStarSpawnCause,
    world: ServerWorld,
    pos: BlockPos,
    influences: MutableList<SpawningInfluence>
) : TriggerSpawningContext(
    cause = cause,
    world = world,
    position = pos,
    light = world.getLightLevel(pos),
    skyLight = world.getLightLevel(pos.up()),
    canSeeSky = world.isSkyVisibleAllowingSea(pos),
    influences = influences
) {

}