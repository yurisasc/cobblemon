package com.cablemc.pokemoncobbled.client.render.models.blockbench.animation

import com.cablemc.pokemoncobbled.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.client.render.models.blockbench.frame.BiWingedFrame
import com.cablemc.pokemoncobbled.client.render.models.blockbench.setRotation
import com.cablemc.pokemoncobbled.client.render.models.blockbench.wavefunction.WaveFunction
import net.minecraft.world.entity.Entity

class WingFlapIdleAnimation<T : Entity>(
    frame: BiWingedFrame,
    val flapFunction: WaveFunction,
    val axis: Int
) : StatelessAnimation<T, BiWingedFrame>(frame) {
    override val targetFrame = BiWingedFrame::class.java

    override fun setAngles(entity: T?, model: PoseableEntityModel<T>, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, headYaw: Float, headPitch: Float) {
        val seconds = entity?.let { model.getState(it).animationSeconds } ?: 0F
        frame.leftWing.setRotation(axis, flapFunction(seconds))
        frame.rightWing.setRotation(axis, -flapFunction(seconds))
    }
}