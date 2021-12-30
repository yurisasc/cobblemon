package com.cablemc.pokemoncobbled.client.render.models.blockbench.bedrock.animation

import com.cablemc.pokemoncobbled.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.client.render.models.blockbench.animation.StatelessAnimation
import com.cablemc.pokemoncobbled.client.render.models.blockbench.frame.ModelFrame
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import com.mojang.math.Vector3d
import net.minecraft.world.entity.Entity
import kotlin.random.Random

class BedrockStatelessAnimation<T: Entity>(frame: ModelFrame, val animation: BedrockAnimationSchema) : StatelessAnimation<T, ModelFrame>(frame) {
    override val targetFrame: Class<ModelFrame> = ModelFrame::class.java

    override fun setAngles(entity: T?, model: PoseableEntityModel<T>, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, headYaw: Float, headPitch: Float) {
        var animationTick = (entity?.let { model.getState(it).animationTick } ?: 0F) / 40
        if (animation.shouldLoop) {
            animationTick = (animationTick % animation.animationLength).toFloat()
        }
        animation.bones.forEach { (boneName, transformations) ->
            val part = model.relevantPartsByName[boneName]
            if (part != null) {
                if (transformations.positionsByKeyFrame.isNotEmpty()) {
                    val positionContext = getInterpolationContext(transformations.positionsByKeyFrame, animationTick)
                    val position = interpolate(positionContext)
                    part.modelPart.apply {
                        x += position.x.toFloat()
                        y += position.y.toFloat()
                        z += position.z.toFloat()
                    }
                }

                if (transformations.rotationsByKeyFrame.isNotEmpty()) {
                    val rotationContext = getInterpolationContext(transformations.rotationsByKeyFrame, animationTick)
                    val rotation = interpolate(rotationContext)
                    part.modelPart.apply {
                        xRot += rotation.x.toFloat().toRadians()
                        yRot += rotation.y.toFloat().toRadians()
                        zRot += rotation.z.toFloat().toRadians()
                    }
                }
            }
        }
    }

    private fun getInterpolationContext(map: Map<Double, Vector3d>, animationTick: Float): InterpolationContext {
        var nextFrameIndex: Int? = null
        val previousFrameIndex: Int

        val sortedTransforms = map.entries.sortedBy { it.key }
        when (val nextIndex = sortedTransforms.indexOfFirst { it.key >= animationTick }) {
            -1 -> {
                previousFrameIndex = sortedTransforms.size - 1
            }
            0 -> {
                previousFrameIndex = sortedTransforms.size - 1
            }
            else -> {
                previousFrameIndex = nextIndex - 1
                nextFrameIndex = nextIndex
            }
        }

        val previous = sortedTransforms[previousFrameIndex].value
        val next = if (nextFrameIndex != null) sortedTransforms[nextFrameIndex].value else null
        val difference = if (nextFrameIndex != null) sortedTransforms[nextFrameIndex].key - sortedTransforms[previousFrameIndex].key else null
        val phaseShift = if (nextFrameIndex != null) sortedTransforms[nextFrameIndex].key - animationTick else null

        return InterpolationContext(previous, next, difference?.toFloat(), phaseShift?.toFloat())
    }

    private fun interpolate(context: InterpolationContext): Vector3d {
        if (context.after != null) {
            return Vector3d(
                    context.before.x + ((context.after.x - context.before.x) * ((context.difference!! - context.phaseShift!!) / context.difference)),
                    context.before.y + ((context.after.y - context.before.y) * ((context.difference - context.phaseShift) / context.difference)),
                    context.before.z + ((context.after.z - context.before.z) * ((context.difference - context.phaseShift) / context.difference)),
            )
        }
        else {
            return context.before
        }
    }
}

private data class InterpolationContext(
    val before: Vector3d,
    val after: Vector3d?,
    val difference: Float?,
    val phaseShift: Float?
)