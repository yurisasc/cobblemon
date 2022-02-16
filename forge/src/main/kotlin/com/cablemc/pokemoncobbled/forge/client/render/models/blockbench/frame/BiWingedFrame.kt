package com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.frame

import com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.PoseableEntityState
import com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.animation.WingFlapIdleAnimation
import com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.wavefunction.WaveFunction
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.world.entity.Entity

interface BiWingedFrame : ModelFrame {
    val leftWing: ModelPart
    val rightWing: ModelPart

    fun <T : Entity> wingFlap(
        flapFunction: WaveFunction,
        timeVariable: (state: PoseableEntityState<T>?, limbSwing: Float, ageInTicks: Float) -> Float?,
        axis: Int
    ) = WingFlapIdleAnimation(
        frame = this,
        flapFunction = flapFunction,
        timeVariable = timeVariable,
        axis = axis
    )
}