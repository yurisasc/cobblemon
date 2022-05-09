package com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.pages.info

import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.SoundlessWidget
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText

class InfoWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    private val pokemon: Pokemon
): SoundlessWidget(pX, pY, pWidth, pHeight, LiteralText("InfoWidget")) {

    override fun render(pMatrixStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        // Rendering Info Texture
        RenderSystem.setShaderTexture(0, infoBaseResource)
        RenderSystem.enableDepthTest()
        drawTexture(pMatrixStack, x, y, 0F, 0F, width, height, width, height)
        if (PokemonCobbledClient.storage.hasPendingEvolutions(this.pokemon)) {
            // ToDo render the button :D
        }
    }

    companion object {
        private val infoBaseResource = cobbledResource("ui/summary/summary_info.png")
    }

}