package com.cobblemon.mod.common.block.multiblock.builder

import com.cobblemon.mod.common.block.multiblock.condition.MultiblockCondition
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Box

/**
 * Represents an area that has a potential to form a MultiBlockStructure
 * @property boundingBox The box that each condition checks in
 * @property conditions The [MultiblockCondition]s that must be met for the multiblock to form. All must be true.
 *
 * @author Apion
 * @since August 24, 2023
 */
interface MultiblockStructureBuilder {
    val boundingBox: Box
    val conditions: List<MultiblockCondition>

    fun validate(world: ServerWorld): Boolean {
        conditions.forEach {
            if (!it.test(world, boundingBox)) {
                return false
            }
        }
        form()
        return true
    }

    fun form()
}
