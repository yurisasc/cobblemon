package com.cablemc.pokemoncobbled.common.spawning.utils

import com.cablemc.pokemoncobbled.common.util.blockPos
import com.cablemc.pokemoncobbled.common.util.toVec3
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.phys.Vec3

object LocationHelper {

    /**
     * Gets the surface. But not the highest possible block => If you build a platform above the surface it spawns below it
     */
    fun getGroundPos(vec3: Vec3, entity: Entity): Vec3 {
        return entity.level.getChunk(vec3.blockPos()).getHeighestPosition(Heightmap.Types.WORLD_SURFACE).toVec3()
    }

}