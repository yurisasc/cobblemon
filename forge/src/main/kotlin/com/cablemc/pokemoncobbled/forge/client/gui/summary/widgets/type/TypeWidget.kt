package com.cablemc.pokemoncobbled.forge.client.gui.summary.widgets.type

import com.cablemc.pokemoncobbled.forge.client.gui.blitk
import com.cablemc.pokemoncobbled.forge.client.gui.summary.widgets.SoundlessWidget
import com.cablemc.pokemoncobbled.common.api.types.ElementalType
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.network.chat.Component

abstract class TypeWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    pMessage: Component
): SoundlessWidget(pX, pY, pWidth, pHeight, pMessage) {

    companion object {
        private val typeResource = cobbledResource("ui/types.png")
        private const val OFFSET = 0.5
    }

    fun renderType(type: ElementalType, pPoseStack: PoseStack, pX: Int = x, pY: Int = y) {
        blitk(
            poseStack = pPoseStack,
            texture = typeResource,
            x = pX + OFFSET, y = pY,
            width = width, height = height,
            uOffset = width * type.textureXMultiplier.toFloat() + 0.1,
            textureWidth = width * 18
        )
    }

    fun renderType(mainType: ElementalType, secondaryType: ElementalType, pPoseStack: PoseStack) {
        renderType(secondaryType, pPoseStack, x + 16)
        renderType(mainType, pPoseStack)
    }
}