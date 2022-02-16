package com.cablemc.pokemoncobbled.common.api.pokeball.catching

data class CaptureContext(
    val numberOfShakes: Int,
    val isSuccessfulCapture: Boolean,
    val isCriticalCapture: Boolean
)