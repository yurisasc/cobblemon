package com.cobblemon.mod.common.item.components.pokeballs

import com.mojang.serialization.Codec

data class GuaranteedCaptureComponent(val guaranteed: Boolean) {
    companion object {
        val CODEC: Codec<GuaranteedCaptureComponent> = Codec.BOOL.xmap(
            { GuaranteedCaptureComponent(it) },
            { it.guaranteed }
        )
    }
}
