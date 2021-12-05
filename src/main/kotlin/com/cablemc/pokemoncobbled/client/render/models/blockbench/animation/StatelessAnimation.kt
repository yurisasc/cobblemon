package com.cablemc.pokemoncobbled.client.render.models.blockbench.animation

import com.cablemc.pokemoncobbled.client.render.models.blockbench.frame.ModelFrame
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.Pose
import net.minecraft.world.entity.Entity

interface StatelessAnimation<T : Entity, in F : ModelFrame> {
    fun setAngles(entity: T, frame: F, pose: Pose<T, F>, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float)
}