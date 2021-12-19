package com.cablemc.pokemoncobbled.client.gui.summary.widgets.type

import com.cablemc.pokemoncobbled.client.gui.summary.mock.ElementalType
import com.cablemc.pokemoncobbled.client.gui.summary.widgets.SoundlessWidget
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

abstract class TypeWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    pMessage: Component
): SoundlessWidget(pX, pY, pWidth, pHeight, pMessage) {

    companion object {
        private val typeResource = ResourceLocation(PokemonCobbled.MODID, "ui/types.png")
    }

    fun renderType(type: ElementalType, pPoseStack: PoseStack, pX: Int = x, pY: Int = y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader)
        RenderSystem.setShaderTexture(0, typeResource)
        blit(
            pPoseStack, pX, pY,
            width * type.xMultiplier.toFloat(), 0F, width, height , width * 18, height
        )
    }

    fun renderType(mainType: ElementalType, secondaryType: ElementalType, pPoseStack: PoseStack) {
        renderType(secondaryType, pPoseStack, x + 16)
        renderType(mainType, pPoseStack)
    }
}