package com.cablemc.pokemoncobbled.client.render.models.blockbench.animation

import com.cablemc.pokemoncobbled.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.client.render.models.blockbench.PoseableEntityState
import com.cablemc.pokemoncobbled.client.render.models.blockbench.frame.ModelFrame
import com.cablemc.pokemoncobbled.client.render.models.blockbench.setRotation
import com.cablemc.pokemoncobbled.client.render.models.blockbench.wavefunction.WaveFunction
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.world.entity.Entity

/**
 * Animation simply works by moving a part's rotation along a particular function
 */
class RotationFunctionStatelessAnimation<T : Entity>(
    val part: ModelPart,
    val function: WaveFunction,
    val axis: Int,
    val timeVariable: (state: PoseableEntityState<T>?, limbSwing: Float, ageInTicks: Float) -> Float?,
    frame: ModelFrame
) : StatelessAnimation<T, ModelFrame>(frame) {
    override val targetFrame = ModelFrame::class.java
    override fun setAngles(entity: T?, model: PoseableEntityModel<T>, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, headYaw: Float, headPitch: Float) {
        part.setRotation(axis, function(timeVariable(entity?.let { model.getState(it) }, limbSwing, ageInTicks) ?: 0F))
    }
}