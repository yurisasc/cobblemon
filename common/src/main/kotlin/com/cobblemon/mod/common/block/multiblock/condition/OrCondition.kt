package com.cobblemon.mod.common.block.multiblock.condition

import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Box
import net.minecraft.util.shape.VoxelShape

class OrCondition(
    val conditionOne: MultiblockCondition,
    val conditionTwo: MultiblockCondition
) : MultiblockCondition {
    override fun test(world: ServerWorld, box: VoxelShape): Boolean {
        return conditionOne.test(world, box) or conditionTwo.test(world, box)
    }

}
