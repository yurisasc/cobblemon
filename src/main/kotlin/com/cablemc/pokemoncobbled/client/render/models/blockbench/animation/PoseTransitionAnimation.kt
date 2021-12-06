package com.cablemc.pokemoncobbled.client.render.models.blockbench.animation

import com.cablemc.pokemoncobbled.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.client.render.models.blockbench.frame.ModelFrame
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.Pose
import com.cablemc.pokemoncobbled.client.render.models.blockbench.withPosition
import com.cablemc.pokemoncobbled.client.render.pokemon.PokemonRenderer.Companion.DELTA_TICKS
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.world.entity.Entity
import java.lang.Float.min

/**
 * An animation that gradually moves any [ModelFrame] from one pose to another.
 *
 * @author Hiroku
 * @since December 5th, 2021
 */
class PoseTransitionAnimation<T : Entity>(
    frame: ModelFrame,
    beforePose: Pose<T, *>,
    val afterPose: Pose<T, *>,
    private val durationTicks: Int = 20
) : StatefulAnimation<T, ModelFrame>(frame) {
    var passedTicks = 0F

    val transforms = mutableListOf<GradualTransform>()

    inner class GradualTransform(
        val modelPart: ModelPart,
        val initialPosition: FloatArray,
        val initialRotation: FloatArray,
        val destinationPosition: FloatArray,
        val destinationRotation: FloatArray
    ) {
        fun apply(ratio: Float) {
            modelPart.setPos(
                ratio * (destinationPosition[0] - initialPosition[0]) + initialPosition[0],
                ratio * (destinationPosition[1] - initialPosition[1]) + initialPosition[1],
                ratio * (destinationPosition[2] - initialPosition[2]) + initialPosition[2]
            )
            modelPart.setRotation(
                ratio * (destinationRotation[0] - initialRotation[0]) + initialRotation[0],
                ratio * (destinationRotation[1] - initialRotation[1]) + initialRotation[1],
                ratio * (destinationRotation[2] - initialRotation[2]) + initialRotation[2]
            )
        }
    }

    init {
        val beforeTransforms = beforePose.transformedParts
        val afterTransforms = afterPose.transformedParts

        val checkedParts = mutableListOf<ModelPart>()

        beforeTransforms.forEach { before ->
            val destination = afterTransforms.find { it.modelPart == before.modelPart }
                ?: before.modelPart
                    .withPosition(before.initialPosition[0], before.initialPosition[1], before.initialPosition[2])
                    .withRotation(before.initialRotation[0], before.initialRotation[1], before.initialRotation[2])

            transforms.add(
                GradualTransform(
                    modelPart = before.modelPart,
                    initialPosition = before.position,
                    initialRotation = before.rotation,
                    destinationPosition = destination.position,
                    destinationRotation = destination.rotation
                )
            )

            checkedParts.add(before.modelPart)
        }

        afterTransforms.filter { it.modelPart !in checkedParts }.forEach { after ->
            transforms.add(
                GradualTransform(
                    modelPart = after.modelPart,
                    initialPosition = after.initialPosition,
                    initialRotation = after.initialRotation,
                    destinationPosition = after.position,
                    destinationRotation = after.rotation
                )
            )
        }
    }

    override fun preventsIdle(entity: T, idleAnimation: StatelessAnimation<T, *>) = true
    override fun run(entity: T, model: PoseableEntityModel<T>): Boolean {
        passedTicks = min(passedTicks + DELTA_TICKS, durationTicks.toFloat())

        val ratio = passedTicks / durationTicks
        transforms.forEach { it.apply(ratio) }

        if (passedTicks == durationTicks.toFloat()) {
            model.getState(entity).setPose(afterPose.poseType)
            return false
        }
        return true
    }
}