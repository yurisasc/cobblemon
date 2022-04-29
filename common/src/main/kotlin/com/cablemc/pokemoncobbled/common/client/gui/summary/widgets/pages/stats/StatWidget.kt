package com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.pages.stats

import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.SoundlessWidget
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.MatrixStack
import net.minecraft.network.chat.LiteralText

class StatWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int
): SoundlessWidget(pX, pY, pWidth, pHeight, LiteralText("StatWidget")) {

    companion object {
        private val statBaseResource = cobbledResource("ui/summary/summary_stats.png")
    }

    override fun render(pMatrixStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        // Rendering Stat Texture
        RenderSystem.setShaderTexture(0, statBaseResource)
        RenderSystem.enableDepthTest()
        blit(pMatrixStack, x, y, 0F, 0F, width, height, width, height)
    }

}