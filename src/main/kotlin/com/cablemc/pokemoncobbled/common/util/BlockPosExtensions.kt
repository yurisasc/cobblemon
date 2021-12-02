package com.cablemc.pokemoncobbled.common.util

import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3

/**
 * For conversion from BlockPos to Vec3
 */
inline fun BlockPos.toVec3(): Vec3 {
    return Vec3(this.x.toDouble(), this.y.toDouble(), this.z.toDouble())
}