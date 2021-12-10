package com.cablemc.pokemoncobbled.client.render.models.blockbench.frame

import com.cablemc.pokemoncobbled.client.render.models.blockbench.animation.WingFlapIdleAnimation
import com.cablemc.pokemoncobbled.client.render.models.blockbench.wavefunction.LineFunction
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.world.entity.Entity

interface BiWingedFrame : ModelFrame {
    val leftWing: ModelPart
    val rightWing: ModelPart

    fun <T : Entity> wingFlap(
        flapFunction: LineFunction,
        axis: Int
    ) = WingFlapIdleAnimation<T>(
        frame = this,
        flapFunction = flapFunction,
        axis = axis
    )
}