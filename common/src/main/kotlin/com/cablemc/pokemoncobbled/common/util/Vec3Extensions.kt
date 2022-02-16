package com.cablemc.pokemoncobbled.common.util

import com.mojang.math.Vector3f
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3

/**
 * For conversion from Vec3 to BlockPos, loses accuracy
 */
fun Vec3.toBlockPos(): BlockPos {
    return BlockPos(this)
}

fun Vec3.toVector3f(): Vector3f = Vector3f(x.toFloat(), y.toFloat(), z.toFloat())
fun Vector3f.toVec3(): Vec3 = Vec3(x().toDouble(), y().toDouble(), z().toDouble())