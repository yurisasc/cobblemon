package com.cobblemon.mod.common.block.multiblock.condition

import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Box

/**
 *
 */
interface MultiblockCondition {
    fun test(world: ServerWorld, box: Box): Boolean
}
