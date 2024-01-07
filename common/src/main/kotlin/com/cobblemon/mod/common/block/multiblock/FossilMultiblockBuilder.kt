/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block.multiblock

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonBlocks
import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.multiblock.MultiblockEntity
import com.cobblemon.mod.common.api.multiblock.builder.MultiblockStructureBuilder
import com.cobblemon.mod.common.block.entity.fossil.FossilTubeBlockEntity
import com.cobblemon.mod.common.block.fossilmachine.FossilCompartmentBlock
import com.cobblemon.mod.common.block.fossilmachine.FossilTubeBlock
import com.cobblemon.mod.common.api.multiblock.condition.BlockRelativeCondition
import com.cobblemon.mod.common.block.entity.fossil.FossilCompartmentBlockEntity
import com.cobblemon.mod.common.util.DataKeys
import com.cobblemon.mod.common.util.blockPositionsAsList
import net.minecraft.nbt.NbtCompound
import net.minecraft.predicate.BlockPredicate
import net.minecraft.predicate.StatePredicate
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes

class FossilMultiblockBuilder(val centerPos: BlockPos) : MultiblockStructureBuilder {
    override val boundingBox: VoxelShape =
        VoxelShapes.union(
            VoxelShapes.cuboid(
                centerPos.x - 1.toDouble(),
                centerPos.y - 1.toDouble(),
                centerPos.z.toDouble(),
                centerPos.x + 2.toDouble(),
                centerPos.y + 2.toDouble(),
                centerPos.z + 1.toDouble()
            ),
            VoxelShapes.cuboid(
                centerPos.x.toDouble(),
                centerPos.y - 1.toDouble(),
                centerPos.z - 1.toDouble(),
                centerPos.x + 1.toDouble(),
                centerPos.y + 2.toDouble(),
                centerPos.z + 2.toDouble()
            )
        )

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
        val dirsToCheck = listOf(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)
        /*
        The process of identifying the constituent blocks is a bit of a clusterfuck so here's an explanation

        1. We find all of the monitors in the boundingBox
        2. We map those positions to a new list representing valid compartment positions
        (This is where it starts getting complicated) We want valid compartment positions, but we
        need to remember the index in the monitor position list that each compartment is valid for.
        So basically we take the monitor positions, check if they have a compartment underneath.
        If they do, set the element in the NEW list to be the position of the compartment.
        If they dont, set the element in the new list to be null
        3. Do a similar process for tubes.
        (Make a copy of the compartment positions, set them to the tube position if theres a tube, null if not)

        What this ends up doing is giving us a list at the end filled with either nulls or BlockPositions
        of tubes that have a valid structure. The index of any tube in the list corresponds to the index
        of the Monitor/Compartment in their respective lists. So if you have a fossilTubePositions list of
        [null BlockPos null], the fossilCompartment BlockPos is fossilCompPositions[1] and the monitor BlockPos
        is fossilMonitorPositions[1]

            - Apion
         */
        var fossilMonitorPositions = blocks.filter {
            FOSSIL_MONITOR_PRED.test(world, it)
        }

        var fossilCompPositions = fossilMonitorPositions.map {
            if (FOSSIL_COMPARTMENT_PRED.test(world, it.down())) it.down()
            else null
        }

        var fossilTubePositions = fossilCompPositions.map { compPosition ->
            if (compPosition == null) {
                return@map null
            }
            dirsToCheck.forEach {
                if (FOSSIL_TUBE_PRED.test(world, compPosition.offset(it))) {
                    return@map compPosition.offset(it)
                }
            }
            return@map null
        }

        val fossilTubeIndex = fossilTubePositions.indexOfFirst {
            it != null
        }
        if (fossilTubeIndex == -1) {
            Cobblemon.LOGGER.error("FossilMultiblockBuilder form called on invalid structure! This should never happen!")
            return
        }
        val fossilMonitorPos = fossilMonitorPositions[fossilTubeIndex]
        val fossilCompPos = fossilCompPositions[fossilTubeIndex]!!
        val fossilTubePos = fossilTubePositions[fossilTubeIndex]!!
        val monitorEntity = world.getBlockEntity(fossilMonitorPos) as MultiblockEntity
        val compEntity = world.getBlockEntity(fossilCompPos) as MultiblockEntity
        val tubeBaseEntity = world.getBlockEntity(fossilTubePos) as MultiblockEntity
        val tubeTopEntity = world.getBlockEntity(fossilTubePos.up()) as MultiblockEntity
        val structure = FossilMultiblockStructure(fossilMonitorPos, fossilCompPos, fossilTubePos)

        structure.tubeConnectorDirection = dirsToCheck.filter {
            val adjPos = fossilTubePos.offset(it)
            return@filter adjPos == fossilCompPos
        }.first()

        compEntity.multiblockStructure = structure
        tubeBaseEntity.multiblockStructure = structure
        tubeTopEntity.multiblockStructure = structure
        monitorEntity.multiblockStructure = structure
        structure.syncToClient(world)
        structure.markDirty(world)

        world.playSound(null, centerPos, CobblemonSounds.FOSSIL_MACHINE_ASSEMBLE, SoundCategory.BLOCKS)

        //Set these to null so the builders can be freed
        compEntity.multiblockBuilder = null
        tubeBaseEntity.multiblockBuilder = null
        tubeTopEntity.multiblockBuilder = null
        monitorEntity.multiblockBuilder = null
    }

    companion object {
        val NBT_TO_CHECK = run {
            val nbt = NbtCompound()
            nbt.putBoolean(DataKeys.FORMED, false)
            return@run nbt
        }
        val FOSSIL_MONITOR_PRED = BlockPredicate.Builder.create().blocks(CobblemonBlocks.FOSSIL_MONITOR)
            .nbt(NBT_TO_CHECK)
            .build()
        val FOSSIL_COMPARTMENT_PRED = BlockPredicate.Builder.create()
            .blocks(CobblemonBlocks.FOSSIL_COMPARTMENT)
            .nbt(NBT_TO_CHECK)
            .build()

        val FOSSIL_TUBE_PRED = BlockPredicate.Builder.create()
            .nbt(NBT_TO_CHECK)
            .blocks(CobblemonBlocks.FOSSIL_TUBE)
            .state(StatePredicate.Builder.create().exactMatch(FossilTubeBlock.PART, FossilTubeBlock.TubePart.BOTTOM).build())
            .build()
        //lol thanks mojang for not allowing nbt puts to be chained
    }

}
