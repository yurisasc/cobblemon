package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.ModelFrame
import net.minecraft.entity.Entity

/**
 * An animation that can run without an entity associated. These are
 * locked to a specific frame, and CAN be given an entity along with
 * limb swing and age information, but might not.
 *
 * @author Hiroku
 * @since December 4th, 2021
 */
abstract class StatelessAnimation<T : Entity, F : ModelFrame>(val frame: F) {
    abstract val targetFrame: Class<F>
    protected abstract fun setAngles(entity: T?, model: PoseableEntityModel<T>, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, headYaw: Float, headPitch: Float)

    fun apply(entity: T?, model: PoseableEntityModel<T>, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, headYaw: Float, headPitch: Float) {
        setAngles(entity, model, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch)
    }
}