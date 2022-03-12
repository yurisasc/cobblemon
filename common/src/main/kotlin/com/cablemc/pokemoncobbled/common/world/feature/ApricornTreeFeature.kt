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

        // Create chunk
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

        return true;
    }

    private fun setBlockIfClear(worldGenLevel: WorldGenLevel, blockPos: BlockPos, blockState: BlockState) {
        if(!TreeFeature.isAirOrLeaves(worldGenLevel, blockPos)) {
            return
        }
        worldGenLevel.level.setBlock(blockPos, blockState, 19)
    }

}