package com.cobblemon.mod.common.block.multiblock.builder

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonBlocks
import com.cobblemon.mod.common.block.entity.MultiblockEntity
import com.cobblemon.mod.common.block.fossilmachine.FossilTubeBlock
import com.cobblemon.mod.common.block.multiblock.FossilMultiblockStructure
import com.cobblemon.mod.common.block.multiblock.condition.BlockRelativeCondition
import com.cobblemon.mod.common.block.multiblock.condition.OrCondition
import com.cobblemon.mod.common.util.math.geometry.blockPositionsAsList
import com.cobblemon.mod.common.util.toVec3d
import net.minecraft.predicate.BlockPredicate
import net.minecraft.predicate.StatePredicate
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction

class ResurrectionMachineMultiblockBuilder(val centerPos: BlockPos) : MultiblockStructureBuilder {
    override val boundingBox: Box = Box.of(centerPos.toVec3d(), 2.0, 2.0, 2.0)
    override val conditions = listOf(
        BlockRelativeCondition(
            FOSSIL_COMPARTMENT_PRED,
            FOSSIL_MONITOR_PRED,
            arrayOf(Direction.UP)
        ),
        BlockRelativeCondition(
            FOSSIL_COMPARTMENT_PRED,
            FOSSIL_TUBE_PRED,
            arrayOf(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST)
        )
    )

    override fun form(world: ServerWorld) {
        //We want to create a MultiblockStructure here and pass a reference to it in every constituent block's entity
        val blocks = boundingBox.blockPositionsAsList()
        val fossilMonitorPositions = blocks.filter { FOSSIL_MONITOR_PRED.test(world, it) }
        val fossilCompPositions = blocks.filter { FOSSIL_COMPARTMENT_PRED.test(world, it) }
        val fossilTubePositions = blocks.filter { FOSSIL_TUBE_PRED.test(world, it) }
        if (fossilMonitorPositions.count() > 1 || fossilCompPositions.count() > 1 || fossilTubePositions.count() > 1) {
            Cobblemon.LOGGER.warn("There are multiple potential formations for the resurrection machine, failing to form")
            return
        }
        val fossilMonitorPos = fossilMonitorPositions.random()
        val fossilCompPos = fossilCompPositions.random()
        val fossilTubePos = fossilTubePositions.random()
        val monitorEntity = world.getBlockEntity(fossilMonitorPos) as MultiblockEntity
        val compEntity = world.getBlockEntity(fossilCompPos) as MultiblockEntity
        val tubeEntity = world.getBlockEntity(fossilTubePos) as MultiblockEntity
        val structure = FossilMultiblockStructure(fossilMonitorPos, fossilCompPos, fossilTubePos)
        compEntity.multiblockStructure = structure
        tubeEntity.multiblockStructure = structure
        monitorEntity.multiblockStructure = structure
        //Set these to null so the builders can be freed
        compEntity.multiblockBuilder = null
        tubeEntity.multiblockBuilder = null
        monitorEntity.multiblockBuilder = null
    }

    companion object {
        val FOSSIL_MONITOR_PRED = BlockPredicate.Builder.create().blocks(CobblemonBlocks.FOSSIL_MONITOR).build()
        val FOSSIL_COMPARTMENT_PRED = BlockPredicate.Builder.create().blocks(CobblemonBlocks.FOSSIL_COMPARTMENT).build()
        val FOSSIL_TUBE_PRED = BlockPredicate.Builder.create()
            .blocks(CobblemonBlocks.FOSSIL_TUBE)
            .state(StatePredicate.Builder.create().exactMatch(FossilTubeBlock.PART, FossilTubeBlock.TubePart.BOTTOM).build())
            .build()

    }

}
