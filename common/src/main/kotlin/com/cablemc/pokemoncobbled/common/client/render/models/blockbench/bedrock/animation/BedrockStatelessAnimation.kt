package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.bedrock.animation

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityState
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.StatelessAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.ModelFrame
import net.minecraft.entity.Entity

/**
 * Animation that analyzes a [BedrockAnimation] and applies transformations to the model based on
 * the given animation time.
 *
 * @param frame The model frame to apply the animation to
 * @param animation The [BedrockAnimation] to be played
 *
 * @author landonjw
 * @since January 5th, 2022
 */
class BedrockStatelessAnimation<T: Entity>(frame: ModelFrame, val animation: BedrockAnimation) : StatelessAnimation<T, ModelFrame>(frame) {
    override val targetFrame: Class<ModelFrame> = ModelFrame::class.java

    override fun setAngles(entity: T?, model: PoseableEntityModel<T>, state: PoseableEntityState<T>?, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, headYaw: Float, headPitch: Float) {
        animation.run(model, state, state?.animationSeconds ?: 0F)
    }
}