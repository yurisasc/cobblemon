package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityState
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.addRotation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.BiWingedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.wavefunction.WaveFunction
import net.minecraft.entity.Entity

class WingFlapIdleAnimation<T : Entity>(
    frame: BiWingedFrame,
    val flapFunction: WaveFunction,
    val timeVariable: (state: PoseableEntityState<T>?, limbSwing: Float, ageInTicks: Float) -> Float?,
    val axis: Int
) : StatelessAnimation<T, BiWingedFrame>(frame) {
    override val targetFrame = BiWingedFrame::class.java

    override fun setAngles(entity: T?, model: PoseableEntityModel<T>, state: PoseableEntityState<T>?, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, headYaw: Float, headPitch: Float) {
        val time = timeVariable(state, limbSwing, ageInTicks) ?: 0F
        frame.leftWing.addRotation(axis, model.scaleForPart(frame.leftWing, flapFunction(time)))
        frame.rightWing.addRotation(axis, model.scaleForPart(frame.rightWing, -flapFunction(time)))
    }
}