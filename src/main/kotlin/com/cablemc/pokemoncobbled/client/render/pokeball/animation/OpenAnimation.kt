package com.cablemc.pokemoncobbled.client.render.pokeball.animation

import com.cablemc.pokemoncobbled.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.client.render.models.blockbench.animation.StatefulAnimation
import com.cablemc.pokemoncobbled.client.render.models.blockbench.animation.StatelessAnimation
import com.cablemc.pokemoncobbled.client.render.models.blockbench.frame.PokeBallFrame
import com.cablemc.pokemoncobbled.common.entity.pokeball.EmptyPokeBallEntity
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import com.cablemc.pokemoncobbled.common.util.math.toVec3
import net.minecraft.commands.arguments.EntityAnchorArgument
import net.minecraft.util.Mth
import net.minecraft.util.Mth.PI
import kotlin.math.min

/**
 * Animation that opens a PokéBall upon hitting a Pokémon, and then shuts it.
 *
 * @author Hiroku
 * @since December 24th, 2021
 */
class OpenAnimation : StatefulAnimation<EmptyPokeBallEntity, PokeBallFrame> {
    companion object {
        const val OPEN_START = 0.1F
        const val OPEN_END = 0.3F
        const val CLOSE_START = 1F
        const val CLOSE_END = 1.2F
        const val OPEN_ANGLE = PI / 3F
    }

    var initialized = false
    var startedClosing = false
    var maxPitch = 0F

    override fun preventsIdle(entity: EmptyPokeBallEntity, idleAnimation: StatelessAnimation<EmptyPokeBallEntity, *>) = true
    override fun run(entity: EmptyPokeBallEntity, model: PoseableEntityModel<EmptyPokeBallEntity>): Boolean {
        val frame = model as PokeBallFrame

        if (!initialized) {
            model.getState(entity).animationSeconds = 0F
            initialized = true
        }

        val animationSeconds = model.getState(entity).animationSeconds

        entity.lookAt(EntityAnchorArgument.Anchor.EYES, entity.hitTargetPosition.get().toVec3())
        val xDist = entity.hitTargetPosition.get().x - entity.x
        val zDist = entity.hitTargetPosition.get().z - entity.z
        frame.rootPart.yRot = Mth.atan2(-zDist, xDist).toFloat() + PI / 2
        frame.rootPart.xRot = -entity.xRotO.toRadians() - PI / 2

        val minPitch = -PI

        if (animationSeconds >= OPEN_START && animationSeconds < CLOSE_START) {
            val portion = min((animationSeconds - OPEN_START)/OPEN_END, 1F)
            frame.lid.xRot = -portion * OPEN_ANGLE
        } else if (animationSeconds >= CLOSE_START) {
            if (!startedClosing) {
                startedClosing = true
                maxPitch = frame.rootPart.xRot
            }

            val portion = min((animationSeconds - CLOSE_START) / (CLOSE_END - CLOSE_START), 1F)
            frame.lid.xRot = (portion - 1) * OPEN_ANGLE
            val dist = maxPitch - minPitch
            frame.rootPart.xRot = minPitch + (1 - portion) * dist
        }

        return entity.captureState.get() == EmptyPokeBallEntity.CaptureState.HIT.ordinal.toByte()
    }
}