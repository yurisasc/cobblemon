package com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.pages.info

import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.SoundlessWidget
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.MatrixStack
import net.minecraft.network.chat.LiteralText

class InfoWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int
): SoundlessWidget(pX, pY, pWidth, pHeight, LiteralText("InfoWidget")) {

    companion object {
        private val infoBaseResource = cobbledResource("ui/summary/summary_info.png")
    }

    override fun render(pMatrixStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        // Rendering Info Texture
        RenderSystem.setShaderTexture(0, infoBaseResource)
        RenderSystem.enableDepthTest()
        blit(pMatrixStack, x, y, 0F, 0F, width, height, width, height)
    }

}