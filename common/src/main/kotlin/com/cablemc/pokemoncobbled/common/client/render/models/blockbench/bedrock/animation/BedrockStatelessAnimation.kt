package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.bedrock.animation

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.StatelessAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.ModelFrame
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import com.mojang.math.Vector3d
import net.minecraft.world.entity.Entity
import java.util.*

/**
 * Animation that analyzes a [BedrockAnimation] and applies transformations to the model based on
 * the given animation time.
 *
 * @param frame The model frame to apply the animation to
 * @param animation The [BedrockAnimation] to be played
 *
 * @author landonjw
 * @since  January 5, 2022
 */
class BedrockStatelessAnimation<T: Entity>(frame: ModelFrame, val animation: BedrockAnimation) : StatelessAnimation<T, ModelFrame>(frame) {
    override val targetFrame: Class<ModelFrame> = ModelFrame::class.java

    override fun setAngles(entity: T?, model: PoseableEntityModel<T>, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, headYaw: Float, headPitch: Float) {
        var animationSeconds = (entity?.let { model.getState(it).animationSeconds } ?: 0F).toDouble()
        if (animation.shouldLoop) {
            animationSeconds %= animation.animationLength
        }
        animation.boneTimelines.forEach { (boneName, timeline) ->
            val part = model.relevantPartsByName[boneName]
            if (part != null) {
                if (timeline.position.isNotEmpty()) {
                    val position = interpolate(timeline.position, animationSeconds)
                    part.modelPart.apply {
                        x += position.x.toFloat()
                        y += position.y.toFloat()
                        z += position.z.toFloat()
                    }
                }

                if (timeline.rotation.isNotEmpty()) {
                    val rotation = interpolate(timeline.rotation, animationSeconds)
                    part.modelPart.apply {
                        xRot += rotation.x.toFloat().toRadians()
                        yRot += rotation.y.toFloat().toRadians()
                        zRot += rotation.z.toFloat().toRadians()
                    }
                }
            }
        }
    }

    /**
     * Retrieves the vector to be used for a bones rotation or position based on a keyframe timeline
     * and the current time in the animation.
     *
     * This will interpolate the values differently based on the type of interpolation defined in the
     * [BedrockAnimationKeyFrame].
     *
     * @param timeline The keyframes for a bone to interpolate vector for
     * @param time The current time in the animation
     * @return A vector where the x, y, and z are interpolated based on the given timeline and time
     */
    private fun interpolate(timeline: Map<Double, BedrockAnimationKeyFrame>, time: Double): Vector3d {
        val sortedTimeline = timeline.toSortedMap()

        var afterIndex : Int? = sortedTimeline.keys.indexOfFirst { it > time }
        if (afterIndex == -1) afterIndex = null
        val beforeIndex = when (afterIndex) {
            null -> sortedTimeline.size - 1
            0 -> null
            else -> afterIndex - 1
        }
        val after = sortedTimeline.getAtIndex(afterIndex)
        val before = sortedTimeline.getAtIndex(beforeIndex)

        if (before != null || after != null) {
            if (before != null && before.interpolationType == InterpolationType.SMOOTH || after != null && after.interpolationType == InterpolationType.SMOOTH) {
                when {
                    before != null && after != null -> {
                        val beforePlusIndex = if (beforeIndex == null || beforeIndex == 0) null else beforeIndex - 1
                        val beforePlus = sortedTimeline.getAtIndex(beforePlusIndex)
                        val afterPlusIndex = if (afterIndex == null || afterIndex == timeline.size - 1) null else afterIndex + 1
                        val afterPlus = sortedTimeline.getAtIndex(afterPlusIndex)
                        return catmullromLerp(beforePlus, before, after, afterPlus, time)
                    }
                    before != null -> return before.data
                    after != null -> return after.data
                    else -> throw IllegalStateException()
                }
            }
            else {
                when {
                    before != null && after != null -> {
                        return Vector3d(
                            before.data.x + (after.data.x - before.data.x) * linearLerpAlpha(before.time, after.time, time),
                            before.data.y + (after.data.y - before.data.y) * linearLerpAlpha(before.time, after.time, time),
                            before.data.z + (after.data.z - before.data.z) * linearLerpAlpha(before.time, after.time, time)
                        )
                    }
                    before != null -> return before.data
                    after != null -> return after.data
                    else -> throw IllegalStateException()
                }
            }
        }
        else {
            return Vector3d(0.0, 0.0, 0.0)
        }
    }

    private fun SortedMap<Double, BedrockAnimationKeyFrame>.getAtIndex(index: Int?): BedrockAnimationKeyFrame? {
        if (index == null) return null
        val key = this.keys.elementAtOrNull(index)
        return if (key != null) this[key] else null
    }
}