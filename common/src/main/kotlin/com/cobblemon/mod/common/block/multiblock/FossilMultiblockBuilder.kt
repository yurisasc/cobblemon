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
import com.cobblemon.mod.common.block.RestorationTankBlock
import com.cobblemon.mod.common.api.multiblock.condition.BlockRelativeCondition
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
            FOSSIL_ANALYZER_PRED,
            MONITOR_PRED,
            arrayOf(Direction.UP)
        ),
        BlockRelativeCondition(
            FOSSIL_ANALYZER_PRED,
            RESTORATION_TANK_PRED,
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
        2. We map those positions to a new list representing valid analyzer positions
        (This is where it starts getting complicated) We want valid analyzer positions, but we
        need to remember the index in the monitor position list that each analyzer is valid for.
        So basically we take the monitor positions, check if they have a analyzer underneath.
        If they do, set the element in the NEW list to be the position of the analyzer.
        If they don't, set the element in the new list to be null
        3. Do a similar process for tanks.
        (Make a copy of the analyzer positions, set them to the tank position if there's a tank, null if not)

        What this ends up doing is giving us a list at the end filled with either nulls or BlockPositions
        of tanks that have a valid structure. The index of any tank in the list corresponds to the index
        of the Monitor/Analyzer in their respective lists. So if you have a fossilTankPositions list of
        [null BlockPos null], the fossilAnalyzer BlockPos is fossilCompPositions[1] and the monitor BlockPos
        is monitorPositions[1]

            - Apion
         */
        var monitorPositions = blocks.filter {
            MONITOR_PRED.test(world, it)
        }

        var fossilAnalyzerPositions = monitorPositions.map {
            if (FOSSIL_ANALYZER_PRED.test(world, it.down())) it.down()
            else null
        }

        var restorationTankPositions = fossilAnalyzerPositions.map { analyzerPosition ->
            if (analyzerPosition == null) {
                return@map null
            }
            dirsToCheck.forEach {
                if (RESTORATION_TANK_PRED.test(world, analyzerPosition.offset(it))) {
                    return@map analyzerPosition.offset(it)
                }
            }
            return@map null
        }

        val fossilTankIndex = restorationTankPositions.indexOfFirst {
            it != null
        }
        if (fossilTankIndex == -1) {
            Cobblemon.LOGGER.error("FossilMultiblockBuilder form called on invalid structure! This should never happen!")
            return
        }
        val monitorPos = monitorPositions[fossilTankIndex]
        val fossilAnalyzerPos = fossilAnalyzerPositions[fossilTankIndex]!!
        val restorationTankPos = restorationTankPositions[fossilTankIndex]!!
        val monitorEntity = world.getBlockEntity(monitorPos) as? MultiblockEntity
        val analyzerEntity = world.getBlockEntity(fossilAnalyzerPos) as? MultiblockEntity
        val tankBaseEntity = world.getBlockEntity(restorationTankPos) as? MultiblockEntity
        val tankTopEntity = world.getBlockEntity(restorationTankPos.up()) as? MultiblockEntity
        val structure = FossilMultiblockStructure(monitorPos, fossilAnalyzerPos, restorationTankPos)

        structure.tankConnectorDirection = dirsToCheck.filter {
            val adjPos = restorationTankPos.offset(it)
            return@filter adjPos == fossilAnalyzerPos
        }.first()

        analyzerEntity?.multiblockStructure = structure
        tankBaseEntity?.multiblockStructure = structure
        tankTopEntity?.multiblockStructure = structure
        monitorEntity?.multiblockStructure = structure
        structure.syncToClient(world)
        structure.markDirty(world)

        world.playSound(null, centerPos, CobblemonSounds.FOSSIL_MACHINE_ASSEMBLE, SoundCategory.BLOCKS)

        //Set these to null so the builders can be freed
        analyzerEntity?.multiblockBuilder = null
        tankBaseEntity?.multiblockBuilder = null
        tankTopEntity?.multiblockBuilder = null
        monitorEntity?.multiblockBuilder = null
    }

    companion object {
        val NBT_TO_CHECK = run {
            val nbt = NbtCompound()
            nbt.putBoolean(DataKeys.FORMED, false)
            return@run nbt
        }
        val MONITOR_PRED = BlockPredicate.Builder.create().blocks(CobblemonBlocks.MONITOR)
            .nbt(NBT_TO_CHECK)
            .build()
        val FOSSIL_ANALYZER_PRED = BlockPredicate.Builder.create()
            .blocks(CobblemonBlocks.FOSSIL_ANALYZER)
            .nbt(NBT_TO_CHECK)
            .build()

        val RESTORATION_TANK_PRED = BlockPredicate.Builder.create()
            .nbt(NBT_TO_CHECK)
            .blocks(CobblemonBlocks.RESTORATION_TANK)
            .state(StatePredicate.Builder.create().exactMatch(RestorationTankBlock.PART, RestorationTankBlock.TankPart.BOTTOM))
            .build()
        //lol thanks mojang for not allowing nbt puts to be chained
    }

}