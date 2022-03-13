package com.cablemc.pokemoncobbled.common.world.feature

import com.cablemc.pokemoncobbled.common.CobbledBlocks
import com.google.common.collect.Lists
import com.mojang.serialization.Codec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.WorldGenLevel
import net.minecraft.world.level.block.LeavesBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext
import net.minecraft.world.level.levelgen.feature.TreeFeature
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider
import java.util.*

class ApricornTreeFeature(
    codec: Codec<NoneFeatureConfiguration>
) : Feature<NoneFeatureConfiguration>(codec) {

    override fun place(context: FeaturePlaceContext<NoneFeatureConfiguration>) : Boolean {
        val worldGenLevel: WorldGenLevel = context.level()
        val random: Random = context.random()
        val origin: BlockPos = context.origin()

        // Create trunk
        val logState = CobbledBlocks.APRICORN_LOG.get().defaultBlockState();
        for(y in 0..4) {
            val logPos = origin.relative(Direction.UP, y)
            worldGenLevel.level.setBlock(logPos, logState, 19)
        }

        // Decorate with leaves
        val leafBlock = CobbledBlocks.APRICORN_LEAVES.get().defaultBlockState()

        val layerOnePos = origin.relative(Direction.UP)
        for(direction in Lists.newArrayList(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)) {
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

        for(coords in Lists.newArrayList(Pair(1, 1), Pair(-1, -1), Pair(1, -1), Pair(-1, 1))) {
            var leafPos = layerOnePos.offset(coords.first, 0, coords.second)
            setBlockIfClear(worldGenLevel, leafPos, LeavesBlock.updateDistance(leafBlock, worldGenLevel, leafPos))
            for(offset in 1..3) {
                leafPos = leafPos.above()
                setBlockIfClear(worldGenLevel, leafPos, LeavesBlock.updateDistance(leafBlock, worldGenLevel, leafPos))
            }
        }

        val layerTwoPos = origin.offset(0, 2, 0)
        for(direction in Lists.newArrayList(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)) {
            var leafPos = layerTwoPos.offset(direction.stepX * 2, direction.stepY * 2, direction.stepZ * 2)
            setBlockIfClear(worldGenLevel, leafPos, LeavesBlock.updateDistance(leafBlock, worldGenLevel, leafPos))
            leafPos = leafPos.above()
            setBlockIfClear(worldGenLevel, leafPos, LeavesBlock.updateDistance(leafBlock, worldGenLevel, leafPos))
        }

        for(coords in Lists.newArrayList(Pair(1, 2), Pair(-1, 2), Pair(1, -2), Pair(-2, 1), Pair(2, 1), Pair(-2, -1), Pair(-1, -2), Pair(2, -1))) {
            var leafPos = layerTwoPos.offset(coords.first, 0, coords.second)
            setBlockIfClear(worldGenLevel, leafPos, LeavesBlock.updateDistance(leafBlock, worldGenLevel, leafPos))
            leafPos = leafPos.above()
            setBlockIfClear(worldGenLevel, leafPos, LeavesBlock.updateDistance(leafBlock, worldGenLevel, leafPos))
        }

        // Topper
        val topperPos = origin.offset(0, 5, 0)
        setBlockIfClear(worldGenLevel, topperPos, LeavesBlock.updateDistance(leafBlock, worldGenLevel, topperPos))

        for(direction in Lists.newArrayList(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)) {
            var leafPos = topperPos.relative(direction)
            setBlockIfClear(worldGenLevel, leafPos, LeavesBlock.updateDistance(leafBlock, worldGenLevel, leafPos))
        }

        for(blocks in getLayerFourVariation(origin.relative(Direction.UP, 4), random)) {
            for(block in blocks) {
                setBlockIfClear(worldGenLevel, block, LeavesBlock.updateDistance(leafBlock, worldGenLevel, block))
            }
        }

        return true;
    }

    private fun setBlockIfClear(worldGenLevel: WorldGenLevel, blockPos: BlockPos, blockState: BlockState) {
        if(!TreeFeature.isAirOrLeaves(worldGenLevel, blockPos)) {
            return
        }
        worldGenLevel.level.setBlock(blockPos, blockState, 19)
    }

    private fun getLayerOneVariation(origin: BlockPos, random: Random): Pair<BlockPos, BlockPos> {
        var direction = Direction.NORTH
        when(random.nextInt(4)) {
            1 -> direction = Direction.EAST
            2 -> direction = Direction.SOUTH
            3 -> direction = Direction.WEST
        }
        val posOne = origin.offset(direction.stepX * 2, direction.stepY * 2, direction.stepZ * 2)
        val offset = if(random.nextBoolean()) -1 else 1
        val posTwo = if(direction.stepX == 0) posOne.offset(offset, 0, 0) else posOne.offset(0, 0, offset)
        return Pair(posOne, posTwo)
    }

    private fun getLayerFourVariation(origin: BlockPos, random: Random): List<List<BlockPos>> {
        val variationList = mutableListOf<List<BlockPos>>()
        val usedDirections = mutableListOf<Direction>()

        for(i in 1..random.nextInt(2,4)) {
            var direction: Direction? = null

            while(direction == null || usedDirections.contains(direction)) {
                when(random.nextInt(4)) {
                    0 -> direction = Direction.NORTH
                    1 -> direction = Direction.EAST
                    2 -> direction = Direction.SOUTH
                    3 -> direction = Direction.WEST
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