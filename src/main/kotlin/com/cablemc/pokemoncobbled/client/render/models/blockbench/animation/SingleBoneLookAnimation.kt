package com.cablemc.pokemoncobbled.client.render.models.blockbench.animation

import com.cablemc.pokemoncobbled.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import net.minecraft.world.entity.Entity

/**
 * A very simple animation for [HeadedFrame]s which has the entity look along the head yaw and pitch.
 * This is designed for simple entities where the model only needs to move a single bone to look at a
 * target.
 *
 * @author Hiroku
 * @since December 5th, 2021
 */
class SingleBoneLookAnimation<T : Entity>(frame: HeadedFrame) : StatelessAnimation<T, HeadedFrame>(frame) {
    override val targetFrame: Class<HeadedFrame> = HeadedFrame::class.java
    override fun setAngles(entity: T?, frame: HeadedFrame, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, headYaw: Float, headPitch: Float) {
        frame.head.xRot += headPitch.toRadians()
        frame.head.yRot += headYaw.toRadians()
    }
}