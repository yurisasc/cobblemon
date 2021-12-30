package com.cablemc.pokemoncobbled.client.render.models.blockbench.bedrock.animation

import com.mojang.math.Vector3d

data class BedrockAnimationFrameSchema(
    val rotationsByKeyFrame: Map<Double, Vector3d>,
    val positionsByKeyFrame: Map<Double, Vector3d>,
    val scalarsByKeyFrame: Map<Double, Vector3d>
)