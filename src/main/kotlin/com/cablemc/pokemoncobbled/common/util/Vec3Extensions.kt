package com.cablemc.pokemoncobbled.common.util

import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3

/**
 * For conversion from Vec3 to BlockPos, loses accuracy
 */
inline fun Vec3.blockPos(): BlockPos {
    return BlockPos(this)
}