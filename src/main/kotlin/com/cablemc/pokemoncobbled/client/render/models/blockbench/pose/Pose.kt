package com.cablemc.pokemoncobbled.client.render.models.blockbench.pose

import com.cablemc.pokemoncobbled.client.render.models.blockbench.frame.ModelFrame
import net.minecraft.world.entity.Entity

interface Pose<T : Entity, out F : ModelFrame> {
    val poseType: PoseType

    fun fits(entity: T): Boolean
}