package com.cablemc.pokemoncobbled.client.render.models.blockbench.additives

import com.cablemc.pokemoncobbled.client.render.models.blockbench.addRotation
import com.cablemc.pokemoncobbled.client.render.models.blockbench.frame.EaredFrame
import com.cablemc.pokemoncobbled.client.render.models.blockbench.frame.ModelFrame
import com.cablemc.pokemoncobbled.client.render.models.blockbench.getRotation
import com.cablemc.pokemoncobbled.client.render.pokemon.PokemonRenderer.Companion.DELTA_TICKS
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import net.minecraft.util.Mth.PI
import net.minecraft.util.Mth.sin
import java.lang.Float.min

/** Intensity is between 0 and 1, 1 being that it pushes the ear through to its low range of motion. */
class EarBounceAdditive(val intensity: Float, val durationTicks: Int = 20) : PosedAdditiveAnimation<PokemonEntity> {
    var initialized = false
    var leftStartAngle = 0F
    var rightStartAngle = 0F
    var passedTicks: Float = 0F
    var leftTotalMovement: Float = 0F
    var rightTotalMovement: Float = 0F

    override fun run(entity: PokemonEntity, frame: ModelFrame): Boolean {
        if (frame !is EaredFrame) {
            return false
        }

        if (!initialized) {
            leftStartAngle = frame.leftEarJoint.modelPart.getRotation(frame.leftEarJoint.axis)
            rightStartAngle = frame.rightEarJoint.modelPart.getRotation(frame.rightEarJoint.axis)
            leftTotalMovement = frame.leftEarJoint.rangeOfMotion.low - leftStartAngle
            rightTotalMovement = frame.rightEarJoint.rangeOfMotion.low - rightStartAngle
            initialized = true
        }

        passedTicks = min(passedTicks + DELTA_TICKS, durationTicks.toFloat())

        val ratioInRange = sin(passedTicks / durationTicks * PI)
        val leftApply = ratioInRange * leftTotalMovement * intensity
        val rightApply = ratioInRange * rightTotalMovement * intensity

        frame.leftEarJoint.modelPart.addRotation(frame.leftEarJoint.axis, leftApply)
        frame.rightEarJoint.modelPart.addRotation(frame.rightEarJoint.axis, rightApply)

        return passedTicks < durationTicks
    }
}