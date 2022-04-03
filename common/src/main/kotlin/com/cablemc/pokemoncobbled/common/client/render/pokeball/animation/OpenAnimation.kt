package com.cablemc.pokemoncobbled.common.client.render.pokeball.animation

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.StatelessAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.PokeBallFrame
import com.cablemc.pokemoncobbled.common.entity.pokeball.EmptyPokeBallEntity
import net.minecraft.util.Mth
import net.minecraft.util.Mth.PI
import kotlin.math.min
import kotlin.math.sqrt

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
        const val CLOSE_START = 1.6F
        const val CLOSE_END = 1.8F
        const val OPEN_ANGLE = PI / 3F
    }

    var initialized = false
    var startedClosing = false
    var startedOpening = false
    var maxPitch = 0F

    override fun preventsIdle(entity: EmptyPokeBallEntity, idleAnimation: StatelessAnimation<EmptyPokeBallEntity, *>) = false
    override fun run(entity: EmptyPokeBallEntity, model: PoseableEntityModel<EmptyPokeBallEntity>): Boolean {
        val frame = model as PokeBallFrame

        if (!initialized) {
            model.getState(entity).animationSeconds = 0F
            initialized = true
        }

        val animationSeconds = model.getState(entity).animationSeconds

        val xDist = entity.hitTargetPosition.get().x - entity.x
        val yDist = entity.hitTargetPosition.get().y - entity.y
        val zDist = entity.hitTargetPosition.get().z - entity.z
        val hypotenuseLength = sqrt(xDist * xDist + zDist * zDist)
        frame.rootPart.yRot = Mth.atan2(-zDist, xDist).toFloat() + PI / 2
        frame.rootPart.xRot = Mth.atan2(yDist, hypotenuseLength).toFloat() - PI / 3
        val minPitch = -PI

        if (animationSeconds >= OPEN_START && animationSeconds < CLOSE_START) {
            val portion = min((animationSeconds - OPEN_START)/OPEN_END, 1F)
            frame.lid.xRot = -portion * OPEN_ANGLE

            if (!startedOpening) {
                startedOpening = true
                entity.capturingPokemon?.beamModeEmitter?.emit(2.toByte())
            }
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