package com.cablemc.pokemoncobbled.common.world.level.levelgen.feature

import com.cablemc.pokemoncobbled.common.CobbledBlocks
import com.cablemc.pokemoncobbled.common.util.randomNoCopy
import com.cablemc.pokemoncobbled.common.world.level.block.ApricornBlock
import com.google.common.collect.Lists
import com.mojang.serialization.Codec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Direction.*
import net.minecraft.tags.BlockTags
import net.minecraft.world.level.WorldGenLevel
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING
import net.minecraft.world.level.block.LeavesBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext
import net.minecraft.world.level.levelgen.feature.TreeFeature
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider
import java.util.*

class ApricornTreeFeature(
    codec: Codec<BlockStateConfiguration>
) : Feature<BlockStateConfiguration>(codec) {

    override fun place(context: FeaturePlaceContext<BlockStateConfiguration>) : Boolean {
        val worldGenLevel: WorldGenLevel = context.level()
        val random: Random = context.random()
        val origin: BlockPos = context.origin()

        if(!worldGenLevel.getBlockState(origin.below()).`is`(BlockTags.DIRT)) {
            return false
        }

        // Create trunk
        val logState = CobbledBlocks.APRICORN_LOG.get().defaultBlockState();
        for(y in 0..4) {
            try {
                val logPos = origin.relative(UP, y)
                worldGenLevel.setBlock(logPos, logState, 2)
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }

        // Decorate with leaves
        val allApricornSpots: MutableList<List<Pair<Direction, BlockPos>>> = mutableListOf()
        val leafBlock = CobbledBlocks.APRICORN_LEAVES.get().defaultBlockState()

        val layerOnePos = origin.relative(UP)
        for(direction in listOf(NORTH, EAST, SOUTH, WEST)) {
            var leafPos = layerOnePos.relative(direction)
            setBlockIfClear(worldGenLevel, leafPos, LeavesBlock.updateDistance(leafBlock, worldGenLevel, leafPos))
            for(offset in 1..3) {
                leafPos = leafPos.above()
                setBlockIfClear(worldGenLevel, leafPos, LeavesBlock.updateDistance(leafBlock, worldGenLevel, leafPos))
            }
        }

        val layerOneExtenders = getLayerOneVariation(layerOnePos, random)
        setBlockIfClear(worldGenLevel, layerOneExtenders.first, LeavesBlock.updateDistance(leafBlock, worldGenLevel, layerOneExtenders.first))
        setBlockIfClear(worldGenLevel, layerOneExtenders.second, LeavesBlock.updateDistance(leafBlock, worldGenLevel, layerOneExtenders.second))

        for(coords in listOf(Pair(1, 1), Pair(-1, -1), Pair(1, -1), Pair(-1, 1))) {
            var leafPos = layerOnePos.offset(coords.first, 0, coords.second)
            setBlockIfClear(worldGenLevel, leafPos, LeavesBlock.updateDistance(leafBlock, worldGenLevel, leafPos))
            for(offset in 1..3) {
                leafPos = leafPos.above()
                setBlockIfClear(worldGenLevel, leafPos, LeavesBlock.updateDistance(leafBlock, worldGenLevel, leafPos))
            }
        }

        val layerTwoPos = origin.offset(0, 2, 0)
        for(direction in Lists.newArrayList(NORTH, EAST, SOUTH, WEST)) {
            val apricornSpots = mutableListOf<Pair<Direction, BlockPos>>()
            var leafPos = layerTwoPos.offset(direction.stepX * 2, direction.stepY * 2, direction.stepZ * 2)

            setBlockIfClear(worldGenLevel, leafPos, LeavesBlock.updateDistance(leafBlock, worldGenLevel, leafPos))
            apricornSpots.add(direction.opposite to leafPos.relative(direction))

            leafPos = leafPos.above()
            setBlockIfClear(worldGenLevel, leafPos, LeavesBlock.updateDistance(leafBlock, worldGenLevel, leafPos))
            apricornSpots.add(direction.opposite to leafPos.relative(direction))

            allApricornSpots.add(apricornSpots)
        }

        for(coords in Lists.newArrayList(Pair(1, 2), Pair(-1, 2), Pair(1, -2), Pair(-2, 1), Pair(2, 1), Pair(-2, -1), Pair(-1, -2), Pair(2, -1))) {
            val apricornSpots = mutableListOf<Pair<Direction, BlockPos>>()
            var leafPos = layerTwoPos.offset(coords.first, 0, coords.second)
            setBlockIfClear(worldGenLevel, leafPos, LeavesBlock.updateDistance(leafBlock, worldGenLevel, leafPos))

            for(direction in listOf(NORTH, EAST, SOUTH, WEST)) {
                val apricornPos = leafPos.relative(direction)
                if(TreeFeature.isAir(worldGenLevel, apricornPos)) {
                    apricornSpots.add(direction.opposite to apricornPos)
                }
            }

            leafPos = leafPos.above()
            setBlockIfClear(worldGenLevel, leafPos, LeavesBlock.updateDistance(leafBlock, worldGenLevel, leafPos))

            for(direction in listOf(NORTH, EAST, SOUTH, WEST)) {
                val apricornPos = leafPos.relative(direction)
                if(TreeFeature.isAir(worldGenLevel, apricornPos)) {
                    apricornSpots.add(direction.opposite to apricornPos)
                }
            }

            allApricornSpots.add(apricornSpots)
        }

        // Topper
        val topperPos = origin.offset(0, 5, 0)
        setBlockIfClear(worldGenLevel, topperPos, LeavesBlock.updateDistance(leafBlock, worldGenLevel, topperPos))

        for(direction in Lists.newArrayList(NORTH, EAST, SOUTH, WEST)) {
            val leafPos = topperPos.relative(direction)
            setBlockIfClear(worldGenLevel, leafPos, LeavesBlock.updateDistance(leafBlock, worldGenLevel, leafPos))
        }

        for(blocks in getLayerFourVariation(origin.relative(UP, 4), random)) {
            for(block in blocks) {
                setBlockIfClear(worldGenLevel, block, LeavesBlock.updateDistance(leafBlock, worldGenLevel, block))
            }
        }

        if(allApricornSpots.isNotEmpty()) {
            allApricornSpots.filter { it.isNotEmpty() }
                .randomNoCopy(allApricornSpots.size.coerceAtMost(5))
                .map { it.random() }
                .forEach {
                    setBlockIfClear(worldGenLevel, it.second, context.config().state.setValue(FACING, it.first))
                }
        }
        return true;
    }

    private fun setBlockIfClear(worldGenLevel: WorldGenLevel, blockPos: BlockPos, blockState: BlockState) {
        if(!TreeFeature.isAirOrLeaves(worldGenLevel, blockPos)) {
            return
        }
        worldGenLevel.setBlock(blockPos, blockState, 3)
    }

    private fun getLayerOneVariation(origin: BlockPos, random: Random): Pair<BlockPos, BlockPos> {
        var direction = NORTH
        when(random.nextInt(4)) {
            1 -> direction = EAST
            2 -> direction = SOUTH
            3 -> direction = WEST
        }
        val posOne = origin.offset(direction.stepX * 2, direction.stepY * 2, direction.stepZ * 2)
        val offset = if(random.nextBoolean()) -1 else 1
        val posTwo = if(direction.stepX == 0) posOne.offset(offset, 0, 0) else posOne.offset(0, 0, offset)
        return posOne to posTwo
    }

    private fun getLayerFourVariation(origin: BlockPos, random: Random): List<List<BlockPos>> {
        val variationList = mutableListOf<List<BlockPos>>()
        val usedDirections = mutableListOf<Direction>()

        for(i in 1..random.nextInt(2,4)) {
            var direction: Direction? = null

            while(direction == null || usedDirections.contains(direction)) {
                when(random.nextInt(4)) {
                    0 -> direction = NORTH
                    1 -> direction = EAST
                    2 -> direction = SOUTH
                    3 -> direction = WEST
                }
            }

            val posOne = origin.offset(direction.stepX * 2, direction.stepY * 2, direction.stepZ * 2)
            val offset = if(random.nextBoolean()) -1 else 1
            val posTwo = if(direction.stepX == 0) posOne.offset(offset, 0, 0) else posOne.offset(0, 0, offset)
            if(random.nextInt(3) == 0) {
                variationList.add(listOf(posOne, posTwo))
            } else {
                variationList.add(listOf(if(random.nextBoolean()) posOne else posTwo))
            }
        }
        return variationList
    }

}