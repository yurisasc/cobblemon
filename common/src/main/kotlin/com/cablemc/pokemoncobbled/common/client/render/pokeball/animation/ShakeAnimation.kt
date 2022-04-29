package com.cablemc.pokemoncobbled.common.client.render.pokeball.animation

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.addRotation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.StatelessAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.PokeBallFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.Z_AXIS
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.wavefunction.linearFunction
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.wavefunction.sineFunction
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.wavefunction.times
import com.cablemc.pokemoncobbled.common.entity.pokeball.EmptyPokeBallEntity
import net.minecraft.util.Util.Mth.PI

/**
 * Animation that shakes a PokéBall on the specified axis, with the specified force.
 * This will move it along that axis along a sine function whose highest amplitude is
 * the force multiplied by PI / 3, meaning at force 1 it rotates 60 degrees to either
 * side. This shaking will be dampened to nothing over the course of 1 second.
 *
 * @author Hiroku
 * @since December 25th, 2021
 */
class ShakeAnimation(
    val force: Float,
    val axis: Int = Z_AXIS
) : StatefulAnimation<EmptyPokeBallEntity, PokeBallFrame> {
    val shakeFunction = sineFunction(
        amplitude = force * PI / 3,
        period = 0.7F
    ) * linearFunction(
        gradient = -1F,
        yIntercept = 1F
    )

    var initialized = false

    override fun preventsIdle(entity: EmptyPokeBallEntity, idleAnimation: StatelessAnimation<EmptyPokeBallEntity, *>) = false
    override fun run(entity: EmptyPokeBallEntity, model: PoseableEntityModel<EmptyPokeBallEntity>): Boolean {
        val frame = model as PokeBallFrame
        val state = model.getState(entity)
        if (!initialized) {
            state.animationSeconds = 0F
            initialized = true
        }

        frame.subRoot.addRotation(axis = axis, differenceInRadians = shakeFunction(state.animationSeconds))

        return state.animationSeconds < 1F
    }
}