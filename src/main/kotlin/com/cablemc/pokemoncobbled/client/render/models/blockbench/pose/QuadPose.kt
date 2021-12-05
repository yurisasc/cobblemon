package com.cablemc.pokemoncobbled.client.render.models.blockbench.pose

import com.cablemc.pokemoncobbled.client.render.models.blockbench.frame.QuadrupedFrame
import net.minecraft.world.entity.Entity

open class QuadPose<T : Entity, out F : QuadrupedFrame>(val condition: (T) -> Boolean = { true }) : Pose<T, F> {
    override val poseType = PoseType.WALK
    override fun fits(entity: T) = condition(entity)
}