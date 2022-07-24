package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.bedrock.animation

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityState
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.StatelessAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.ModelFrame
import net.minecraft.entity.Entity

/**
 * A stateful animation that runs a [BedrockAnimation]. It is completed when the underlying
 * bedrock animation is complete.
 *
 * @author Hiroku
 * @since July 16th, 2022
 */
open class BedrockStatefulAnimation<T : Entity>(
    val animation: BedrockAnimation,
    val preventsIdleCheck: (T?, PoseableEntityState<T>, StatelessAnimation<T, *>) -> Boolean
) : StatefulAnimation<T, ModelFrame> {
    val startTime = System.currentTimeMillis()
    override fun preventsIdle(entity: T?, state: PoseableEntityState<T>, idleAnimation: StatelessAnimation<T, *>) = preventsIdleCheck(entity, state, idleAnimation)
    override fun run(
        entity: T?,
        model: PoseableEntityModel<T>,
        state: PoseableEntityState<T>,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        headYaw: Float,
        headPitch: Float
    ) = animation.run(model, state, (System.currentTimeMillis() - startTime) / 1000F)
}