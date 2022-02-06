package com.cablemc.pokemoncobbled.common.spawning

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.spawning.WorldSlice
import com.cablemc.pokemoncobbled.common.api.spawning.prospecting.SpawningProspector
import com.cablemc.pokemoncobbled.common.api.spawning.spawner.Spawner
import com.cablemc.pokemoncobbled.common.api.spawning.spawner.SpawningArea
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Blocks

/**
 * A spawning prospector that takes a straightforward approach
 * in slicing out a [WorldSlice]. If you want to replace this,
 * change over the value of [PokemonCobbled.prospector].
 *
 * @author Hiroku
 * @since February 5th, 2022
 */
object CobbledSpawningProspector : SpawningProspector {
    override fun prospect(
        spawner: Spawner,
        area: SpawningArea
    ): WorldSlice {
        val level = area.level
        var baseY = area.baseY
        var height = area.height
        if (baseY < level.minBuildHeight) {
            val difference = level.minBuildHeight - baseY
            baseY += difference
            height -= difference
            if (height < 1) {
                throw IllegalStateException("World slice was attempted with totally awful base and dimensions")
            }
        }

        if (baseY + height >= level.maxBuildHeight) {
            val difference = baseY + height - 1 - level.maxBuildHeight
            height -= difference
            if (height < 1) {
                throw IllegalStateException("World slice was attempted with totally awful base and dimensions")
            }
        }

        val blocks = Array(area.length) { Array(height) { Array(area.width) { WorldSlice.BlockData(Blocks.STONE.defaultBlockState(), 0) } } }
        val skyLevel = Array(area.length) { Array(area.width) { level.maxBuildHeight } }
        val pos = BlockPos.MutableBlockPos()

        val yRange = (baseY until baseY + height).reversed()
        for (x in area.baseX until area.baseX + area.length) {
            for (z in area.baseZ until area.baseZ + area.width) {
                var skyAbove = true
                for (y in yRange) {
                    val state = level.getBlockState(pos.set(x, y, z))
                    blocks[x][y][z] = WorldSlice.BlockData(
                        state = state,
                        light = state.getLightBlock(level, pos)
                    )

                    // TODO don't just check solid, have some property somewhere modifiable that excludes some blocks from occluding
                    if (skyAbove && state.material.isSolid) {
                        skyAbove = false
                        skyLevel[x][z] = y + 1
                    }
                }
            }
        }

        return WorldSlice(
            cause = area.cause,
            level = level,
            baseX = area.baseX,
            baseY = baseY,
            baseZ = area.baseZ,
            blocks = blocks,
            skyLevel = skyLevel
        )
    }
}