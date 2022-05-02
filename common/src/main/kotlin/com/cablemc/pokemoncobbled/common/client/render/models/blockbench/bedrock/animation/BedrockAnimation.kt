package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.bedrock.animation

import com.eliotlash.molang.expressions.MolangExpression
import net.minecraft.util.math.Vec3d
import java.util.SortedMap

data class BedrockAnimationGroup(
    val formatVersion: String,
    val animations: Map<String, BedrockAnimation>
)

data class BedrockAnimation(
    val shouldLoop: Boolean,
    val animationLength: Double,
    val boneTimelines: Map<String, BedrockBoneTimeline>
)

interface BedrockBoneValue {
    fun resolve(time: Double): Vec3d
    fun isEmpty(): Boolean
}

object EmptyBoneValue : BedrockBoneValue {
    override fun resolve(time: Double) = Vec3d.ZERO
    override fun isEmpty() = true
}

data class BedrockBoneTimeline (
    val position: BedrockBoneValue,
    val rotation: BedrockBoneValue
)

class MolangBoneValue(
    val x: MolangExpression,
    val y: MolangExpression,
    val z: MolangExpression,
    transformation: Transformation
) : BedrockBoneValue {
    val yMul = if (transformation == Transformation.POSITION) -1 else 1
    override fun isEmpty() = false
    override fun resolve(time: Double): Vec3d {
        for (n in arrayOf(x, y, z)) {
            n.context.setValue("q.anim_time", time)
            n.context.setValue("query.anim_time", time)
        }
        return Vec3d(x.get(), y.get() * yMul, z.get())
    }

}

class BedrockKeyFrameBoneValue : HashMap<Double, BedrockAnimationKeyFrame>(), BedrockBoneValue {
    fun SortedMap<Double, BedrockAnimationKeyFrame>.getAtIndex(index: Int?): BedrockAnimationKeyFrame? {
        if (index == null) return null
        val key = this.keys.elementAtOrNull(index)
        return if (key != null) this[key] else null
    }

    override fun resolve(time: Double): Vec3d {
        val sortedTimeline = toSortedMap()

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
                        val afterPlusIndex = if (afterIndex == null || afterIndex == size - 1) null else afterIndex + 1
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
                        return Vec3d(
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
            return Vec3d(0.0, 0.0, 0.0)
        }
    }

}

data class BedrockAnimationKeyFrame(
    val time: Double,
    val transformation: Transformation,
    val data: Vec3d,
    val interpolationType: InterpolationType
)

enum class InterpolationType {
    SMOOTH, LINEAR
}

enum class Transformation {
    POSITION, ROTATION
}