package com.cobblemon.mod.common.api.types

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.util.ColorRGBA

data class ElementalTypeDisplay(
    val tint: ColorRGBA
) {

    companion object {
        @JvmStatic
        val CODEC: Codec<ElementalTypeDisplay> = RecordCodecBuilder.create { instance ->
            instance.group(
                ColorRGBA.CODEC.fieldOf("tint").forGetter(ElementalTypeDisplay::tint)
            ).apply(instance, ::ElementalTypeDisplay)
        }
    }

}