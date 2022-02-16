package com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.animation

import com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.PoseableEntityState
import com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.frame.BiWingedFrame
import com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.setRotation
import com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.wavefunction.WaveFunction
import net.minecraft.world.entity.Entity

class WingFlapIdleAnimation<T : Entity>(
    frame: BiWingedFrame,
    val flapFunction: WaveFunction,
    val timeVariable: (state: PoseableEntityState<T>?, limbSwing: Float, ageInTicks: Float) -> Float?,
    val axis: Int
) : StatelessAnimation<T, BiWingedFrame>(frame) {
    override val targetFrame = BiWingedFrame::class.java

    override fun setAngles(entity: T?, model: PoseableEntityModel<T>, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, headYaw: Float, headPitch: Float) {
        val time = timeVariable(entity?.let { model.getState(it) }, limbSwing, ageInTicks) ?: 0F
        frame.leftWing.setRotation(axis, flapFunction(time))
        frame.rightWing.setRotation(axis, -flapFunction(time))
    }
}