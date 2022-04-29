package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.QuadrupedFrame
import net.minecraft.util.Util.Mth
import net.minecraft.entity.Entity

/**
 * A quadruped animation that will have zero-rotations on all legs at
 * stateless and otherwise does simple predictable walking like Minecraft
 * quadrupeds.
 *
 * @author Hiroku
 * @since December 4th, 2021
 */
class QuadrupedWalkAnimation<T : Entity>(
    frame: QuadrupedFrame,
    /** The multiplier to apply to the cosine movement of the legs. The smaller this value, the quicker the legs move. */
    val periodMultiplier: Float = 0.6662F,
    /** The multiplier to apply to the stride of the entity. The larger this is, the further the legs move. */
    val amplitudeMultiplier: Float = 1.4F
) : StatelessAnimation<T, QuadrupedFrame>(frame) {
    override val targetFrame: Class<QuadrupedFrame> = QuadrupedFrame::class.java
    override fun setAngles(entity: T?, model: PoseableEntityModel<T>, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, headYaw: Float, headPitch: Float) {
        frame.hindRightLeg.xRot += Mth.cos(limbSwing * periodMultiplier) * limbSwingAmount * amplitudeMultiplier
        frame.hindLeftLeg.xRot += Mth.cos(limbSwing * periodMultiplier + Math.PI.toFloat()) * limbSwingAmount * amplitudeMultiplier
        frame.foreRightLeg.xRot += Mth.cos(limbSwing * periodMultiplier + Math.PI.toFloat()) * limbSwingAmount * amplitudeMultiplier
        frame.foreLeftLeg.xRot += Mth.cos(limbSwing * periodMultiplier) * limbSwingAmount * amplitudeMultiplier
    }
}