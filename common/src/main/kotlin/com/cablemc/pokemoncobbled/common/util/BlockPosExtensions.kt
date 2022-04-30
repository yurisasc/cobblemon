package com.cablemc.pokemoncobbled.common.util

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

/**
 * For conversion from BlockPos to Vec3d */
fun BlockPos.toVec3d(): Vec3d {
    return Vec3d(this.x.toDouble(), this.y.toDouble(), this.z.toDouble())
}