package com.cablemc.pokemoncobbled.client.render.models.blockbench.bedrock.animation

import com.google.gson.annotations.SerializedName

data class BedrockAnimationSchema(
    @SerializedName("loop") val shouldLoop: Boolean,
    val animationLength: Double,
    val bones: Map<String, BedrockAnimationFrameSchema>
)