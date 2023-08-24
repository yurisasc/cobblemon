package com.cobblemon.mod.common.block.multiblock.condition

import com.cobblemon.mod.common.util.math.geometry.blockPositionsAsList
import net.minecraft.predicate.BlockPredicate
import net.minecraft.server.world.ServerWorld
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
        val relToBlockBlockPos = box.blockPositionsAsList().filter { relToBlock.test(world, it) }.randomOrNull() ?: return false
        directionsToCheck.forEach {
            if (targetBlock.test(world, relToBlockBlockPos.offset(it))) {
                return true
            }
        }
        return false
    }

}
