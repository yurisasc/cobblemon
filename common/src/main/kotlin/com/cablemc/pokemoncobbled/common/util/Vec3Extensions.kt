package com.cablemc.pokemoncobbled.common.util

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3f

/**
 * For conversion from Vec3dto BlockPos, loses accuracy
 */
fun Vec3d.toBlockPos(): BlockPos {
    return BlockPos(this)
}

fun Vec3d.toVec3f(): Vec3f = Vec3f(x.toFloat(), y.toFloat(), z.toFloat())
fun Vec3f.toVec3d(): Vec3d= Vec3d(x.toDouble(), y.toDouble(), z.toDouble())