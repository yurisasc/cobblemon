package com.cobblemon.mod.common.block.multiblock.condition

import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Box

class AndCondition(val conditionOne: MultiblockCondition, val conditionTwo: MultiblockCondition) : MultiblockCondition {
    override fun test(world: ServerWorld, box: Box): Boolean {
        return conditionOne.test(world, box) and conditionTwo.test(world, box)
    }

}
