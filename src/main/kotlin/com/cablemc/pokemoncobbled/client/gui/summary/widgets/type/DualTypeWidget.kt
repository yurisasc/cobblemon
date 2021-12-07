package com.cablemc.pokemoncobbled.client.gui.summary.widgets.type

import com.cablemc.pokemoncobbled.client.gui.summary.mock.ElementalType
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

class DualTypeWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    pMessage: Component,
    mainType: ElementalType, secondaryType: ElementalType
) : TypeWidget(pX, pY, pWidth, pHeight, pMessage) {

    private val mainTypeResource = ResourceLocation(PokemonCobbled.MODID, "ui/pokemontypes/${mainType.name}.png")
    private val secondaryTypeResource = ResourceLocation(PokemonCobbled.MODID, "ui/pokemontypes/${secondaryType.name}.png")

    override fun render(pPoseStack: PoseStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        // Rendering Main Type
        RenderSystem.setShader(GameRenderer::getPositionTexShader)
        RenderSystem.setShaderTexture(0, mainTypeResource)
        RenderSystem.enableDepthTest()

        super.render(pPoseStack, pMouseX, pMouseY, pPartialTicks)
    }

    override fun updateNarration(pNarrationElementOutput: NarrationElementOutput) {
        TODO("Not yet implemented")
    }
}