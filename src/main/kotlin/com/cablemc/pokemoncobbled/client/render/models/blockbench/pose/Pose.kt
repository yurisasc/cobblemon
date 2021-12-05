package com.cablemc.pokemoncobbled.client.render.models.blockbench.pose

import com.cablemc.pokemoncobbled.client.render.models.blockbench.PoseableEntityState
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
    fun idleStateless(limbSwing: Float = 0F, limbSwingAmount: Float = 0F, ageInTicks: Float = 0F, headYaw: Float = 0F, headPitch: Float = 0F) {
        idleAnimations.forEach { it.apply(null, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch) }
    }

    fun idleStateful(entity: T, state: PoseableEntityState<T>, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, headYaw: Float, headPitch: Float) {
        idleAnimations.forEach { idleAnimation ->
            if (state.statefulAnimations.none { it.preventsIdle(entity, idleAnimation) }) {
                idleAnimation.apply(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch)
            }
        }
    }
}