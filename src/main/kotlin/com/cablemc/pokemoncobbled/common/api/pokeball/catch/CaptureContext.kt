package com.cablemc.pokemoncobbled.common.api.pokeball.catch

data class CaptureContext(
    val numberOfShakes: Int,
    val isSuccessfulCapture: Boolean,
    val isCriticalCapture: Boolean
)