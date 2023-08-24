package com.cobblemon.mod.common.block.multiblock.condition

import net.minecraft.predicate.BlockPredicate
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction

/**
 * Checks that a block adjacent to another block matches a predicate. Basically we find a block that
 * matched relToBlock, then we look in each direction to see if there is an adjacent block that matches
 * target block.
 *
 * @param relToBlock The [BlockPredicate] for the main block. The block that matches this predicate is the block whose adjacent blocks we check
 * @param targetBlock The [BlockPredicate] that is checked for blocks adjacent to relToBlock
 * @param directionsToCheck The [Direction]s that we check from the target relToBlock
 *
 *  @author Apion
 *  @since August 24th, 2023
 */
class BlockRelativeCondition(
    val relToBlock: BlockPredicate,
    val targetBlock: BlockPredicate,
    val directionsToCheck: Array<Direction> = Direction.values()
) : MultiblockCondition {
    override fun test(world: ServerWorld, box: Box): Boolean {
        val relToBlockBlockPos = getRelToBlockPos(world, box) ?: return false
        directionsToCheck.forEach {
            if (targetBlock.test(world, relToBlockBlockPos.offset(it))) {
                return true
            }
        }
        return false
    }

    private fun getRelToBlockPos(world: ServerWorld, box: Box): BlockPos? {
        val minX = box.minX.toInt()
        val minY = box.minY.toInt()
        val minZ = box.minZ.toInt()
        val maxX = box.maxX.toInt()
        val maxY = box.maxY.toInt()
        val maxZ = box.maxZ.toInt()
        for (x in minX..maxX) {
            for (y in minY..maxY) {
                for (z in minZ..maxZ) {
                    val posToCheck = BlockPos(x, y, z)
                    if (relToBlock.test(world, posToCheck)) {
                        return posToCheck
                    }
                }
            }
        }
        return null
    }


}
