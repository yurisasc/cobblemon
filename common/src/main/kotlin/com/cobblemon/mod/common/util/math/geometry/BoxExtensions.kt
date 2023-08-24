package com.cobblemon.mod.common.util.math.geometry

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box

fun Box.blockPositionsAsList(): List<BlockPos> {
    val result = mutableListOf<BlockPos>()
    val minX = minX.toInt()
    val minZ = minZ.toInt()
    val minY = minY.toInt()
    val maxX = maxX.toInt()
    val maxY = maxY.toInt()
    val maxZ = maxZ.toInt()
    for (x in minX..maxX) {
        for (y in minY..maxY) {
            for (z in minZ..maxZ) {
                result.add(BlockPos(x, y, z))
            }
        }
    }
    return result
}
