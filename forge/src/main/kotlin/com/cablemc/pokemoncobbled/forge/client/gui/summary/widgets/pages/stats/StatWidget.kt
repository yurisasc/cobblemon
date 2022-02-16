package com.cablemc.pokemoncobbled.forge.client.gui.summary.widgets.pages.stats

import com.cablemc.pokemoncobbled.forge.client.gui.summary.widgets.SoundlessWidget
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.network.chat.TextComponent

class StatWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int
): SoundlessWidget(pX, pY, pWidth, pHeight, TextComponent("StatWidget")) {

    companion object {
        private val statBaseResource = cobbledResource("ui/summary/summary_stats.png")
    }

    override fun render(pMatrixStack: PoseStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        // Rendering Stat Texture
        RenderSystem.setShaderTexture(0, statBaseResource)
        RenderSystem.enableDepthTest()
        blit(pMatrixStack, x, y, 0F, 0F, width, height, width, height)
    }

}