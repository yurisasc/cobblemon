package com.cablemc.pokemoncobbled.forge.common.api.pokeball.catching

data class CaptureContext(
    val numberOfShakes: Int,
    val isSuccessfulCapture: Boolean,
    val isCriticalCapture: Boolean
)