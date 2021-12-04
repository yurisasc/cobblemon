package com.cablemc.pokemoncobbled.client.render.pokeball.animation

import net.minecraft.client.model.EntityModel

interface ModelAnimation<T : EntityModel<*>> {

    /** The current frame the animation is on. If this is null, the animation is not currently running. */
    val currentFrame: Int?

    /** Animates the model. */
    fun animate(model: T, limbSwing: Float, limbSwingAmout: Float, ageInTicks: Float, netHeadYaw: Float, headPitch: Float, frame: Int = 0)

    fun resetAnimation()

}