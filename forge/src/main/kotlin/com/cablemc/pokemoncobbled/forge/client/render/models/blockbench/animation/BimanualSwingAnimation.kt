package com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.animation

import com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.frame.BimanualFrame
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity

/**
 * A bimanual arm animation that will have more force while moving and idle sway.
 * This creates a simple predictable arm movement like Minecraft bimanuals.
 *
 * @author Deltric
 * @since December 21st, 2021
 */
class BimanualSwingAnimation<T : Entity>(
    frame: BimanualFrame,
    /** The multiplier to apply to the cosine movement of the arms. The smaller this value, the quicker the arms move. */
    val swingPeriodMultiplier: Float = 0.6662F,
    /** The multiplier to apply to the swing of the entity. The larger this is, the further the arms move. */
    val amplitudeMultiplier: Float = 1F
) : StatelessAnimation<T, BimanualFrame>(frame) {
    override val targetFrame: Class<BimanualFrame> = BimanualFrame::class.java
    override fun setAngles(entity: T?, model: PoseableEntityModel<T>, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, headYaw: Float, headPitch: Float) {
        // Movement swing
        frame.rightArm.yRot += Mth.cos(limbSwing * swingPeriodMultiplier) * limbSwingAmount * amplitudeMultiplier
        frame.leftArm.yRot += Mth.cos(limbSwing * swingPeriodMultiplier) * limbSwingAmount * amplitudeMultiplier

        // Idle sway
        frame.rightArm.zRot += 1.0f * (Mth.cos(ageInTicks * 0.09f) * 0.05f + 0.05f)
        frame.rightArm.yRot += 1.0f * Mth.sin(ageInTicks * 0.067f) * 0.05f
        frame.leftArm.zRot += -1.0f * (Mth.cos(ageInTicks * 0.09f) * 0.05f + 0.05f)
        frame.leftArm.yRot += -1.0f * Mth.sin(ageInTicks * 0.067f) * 0.05f
    }
}