package com.cobblemon.mod.common.pokemon.riding

import net.minecraft.util.math.Vec3d

interface RidingDelegator {

    fun tick(movement: Vec3d)

    fun speed(): Float

}