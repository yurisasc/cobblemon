package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityState
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.addPosition
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.addRotation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.ModelFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.wavefunction.WaveFunction
import net.minecraft.client.model.ModelPart
import net.minecraft.entity.Entity
import kotlin.math.atan

/**
 * An animation that interpolates some segment of limbs onto a given wave function. This wave function
 * will probably be a sine or cosine wave in most cases, but any wave can be used.
 *
 * @param frame The frame this is for, but this is not really used meaningfully.
 * @param waveFunction The wave to match the limbs to.
 * @param oscillationsScalar A multiplier used to control how many oscillations will occur across the length of the body
 *                           at once. If this value is multiple times the wave function period, the wave will play out
 *                           multiple times in a single frame along the length of the body.
 * @param head The part representing the head of the wave.
 * @param headLength The length of the head in model terms.
 * @param moveHead Whether the head of the body will move along the wave.
 * @param rotationAxis The axis that will be rotated around to act out the wave.
 * @param motionAxis The axis along which the head will move, if you chose for it to move. This is never the rotation axis.
 * @param basedOnLimbSwing Whether the time variable will be provided by the limb swing. If true, the animation will
 *                         only progress in time when the entity is moving. It also means you need to change the wave
 *                         function as limb swing is based on ticks whereas other methods use seconds.
 * @param segments The array of [WaveSegment]s that make up the wave.
 *
 * @author Hiroku
 * @since December 13, 2021
 */
class WaveAnimation<T : Entity>(
    frame: ModelFrame,
    val waveFunction: WaveFunction,
    val oscillationsScalar: Float,
    val head: ModelPart,
    val headLength: Float,
    val moveHead: Boolean = false,
    val rotationAxis: Int,
    val motionAxis: Int,
    val basedOnLimbSwing: Boolean = false,
    val segments: Array<WaveSegment>
): StatelessAnimation<T, ModelFrame>(frame) {
    override val targetFrame = ModelFrame::class.java

    override fun setAngles(entity: T?, model: PoseableEntityModel<T>, state: PoseableEntityState<T>?, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, headYaw: Float, headPitch: Float) {
        val t = if (basedOnLimbSwing) {
            limbSwing
        } else {
            entity?.let { model.getState(it).animationSeconds } ?: 0F
        }

        var totalTimeDisplacement = (headLength + segments.map { it.length }.sum()) / oscillationsScalar
        if (moveHead) {
            val headDisplacement = waveFunction(t + totalTimeDisplacement - headLength / oscillationsScalar) * 16
            head.addPosition(motionAxis, model.scaleForPart(head, -headDisplacement))
        }

        totalTimeDisplacement -= headLength / oscillationsScalar
        var previousSegmentLength = headLength
        var previousTheta = 0F

        for (segment in segments) {
            val t2 = totalTimeDisplacement + previousSegmentLength / 2 / oscillationsScalar
            val t1 = totalTimeDisplacement - segment.length / 2 / oscillationsScalar

            val yAfter = waveFunction(t + t1)
            val yBefore = waveFunction(t + t2)

            val ratio = (yAfter - yBefore) / (t2 - t1)
            val theta = atan(ratio)

            /*
             * This reversing of the angle by previousTheta is actually just a crude guess.
             * The joint it rotated around previously was a little distance away from this joint, meaning this is wrong.
             * To do this perfectly, we need to calculate the error of this action and apply a
             * translation to this part so that it counters the error. In most cases the error is insignificant.
             */
            segment.modelPart.addRotation(rotationAxis, model.scaleForPart(segment.modelPart, theta - previousTheta))
            previousTheta = theta
            previousSegmentLength = segment.length
            totalTimeDisplacement -= segment.length / oscillationsScalar
            if (totalTimeDisplacement < 0) {
                totalTimeDisplacement = 0F
            }
        }
    }
}

class WaveSegment(
    val modelPart: ModelPart,
    val length: Float
)