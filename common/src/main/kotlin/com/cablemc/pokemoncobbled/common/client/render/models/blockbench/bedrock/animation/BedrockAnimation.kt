package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.bedrock.animation

import net.minecraft.client.util.math.Vector3d

data class BedrockAnimationGroup(
    val formatVersion: String,
    val animations: Map<String, BedrockAnimation>
)

data class BedrockAnimation(
    val shouldLoop: Boolean,
    val animationLength: Double,
    val boneTimelines: Map<String, BedrockBoneTimeline>
)

data class BedrockBoneTimeline (
    val position: Map<Double, BedrockAnimationKeyFrame>,
    val rotation: Map<Double, BedrockAnimationKeyFrame>
)

data class BedrockAnimationKeyFrame(
    val time: Double,
    val transformation: Transformation,
    val data: Vector3d,
    val interpolationType: InterpolationType
)

enum class InterpolationType {
    SMOOTH, LINEAR
}

enum class Transformation {
    POSITION, ROTATION
}