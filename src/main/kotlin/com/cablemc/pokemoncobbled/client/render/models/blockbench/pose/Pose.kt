package com.cablemc.pokemoncobbled.client.render.models.blockbench.pose

import com.cablemc.pokemoncobbled.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.client.render.models.blockbench.animation.StatelessAnimation
import com.cablemc.pokemoncobbled.client.render.models.blockbench.frame.ModelFrame
import net.minecraft.world.entity.Entity

/**
 * A pose for a model.
 */
class Pose<T : Entity, F : ModelFrame>(
    val poseType: PoseType,
    val condition: (T) -> Boolean,
    val idleAnimations: Array<StatelessAnimation<T, out F>>,
    val transformedParts: Array<TransformedModelPart>
) {
    fun idleStateless(model: PoseableEntityModel<T>, limbSwing: Float = 0F, limbSwingAmount: Float = 0F, ageInTicks: Float = 0F, headYaw: Float = 0F, headPitch: Float = 0F) {
        idleAnimations.forEach { it.apply(null, model, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch) }
    }

    fun idleStateful(entity: T, model: PoseableEntityModel<T>, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, headYaw: Float, headPitch: Float) {
        val state = model.getState(entity)
        idleAnimations.forEach { idleAnimation ->
            if (state.statefulAnimations.none { it.preventsIdle(entity, idleAnimation) }) {
                idleAnimation.apply(entity, model, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch)
            }
        }
    }
}