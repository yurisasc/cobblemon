package com.cablemc.pokemoncobbled.client.gui.summary.widgets.pages.moves

import com.cablemc.pokemoncobbled.client.gui.summary.mock.PokemonMove
import com.cablemc.pokemoncobbled.client.gui.summary.widgets.SoundlessWidget
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.network.chat.TextComponent
import net.minecraft.resources.ResourceLocation

class MoveWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    val move: PokemonMove
): SoundlessWidget(pX, pY, pWidth, pHeight, TextComponent(move.name)) {

    companion object {
        private val moveResource = ResourceLocation(PokemonCobbled.MODID, "ui/summary/summary_moves_slot.png")
        private val movePpResource = ResourceLocation(PokemonCobbled.MODID, "ui/summary/summary_moves_overlay_pp.png")
        private const val ppWidthDiff = 2
        private const val ppHeight = 7
        private const val ppHeightDiff = 22
    }

    override fun render(pMatrixStack: PoseStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        // Rendering Move Texture
        RenderSystem.setShaderTexture(0, moveResource)
        //RenderSystem.enableDepthTest()
        blit(pMatrixStack, x, y, 0F, 0F, width, height, width, height)

        // Rendering PP Texture
        RenderSystem.setShaderTexture(0, movePpResource)
        RenderSystem.enableDepthTest()
        //blit(pMatrixStack, x + ppWidthDiff, y + ppHeightDiff, 0F, 0F, width - ppWidthDiff * 2 - 100, ppHeight, width - ppWidthDiff * 2, ppHeight)
    }

    private fun getPpAsPercentage(move: PokemonMove): Double {
        return move.curPp.toDouble() / move.maxPp.toDouble()
    }
}