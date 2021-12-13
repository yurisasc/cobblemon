package com.cablemc.pokemoncobbled.client.gui.summary.widgets.pages.moves

import com.cablemc.pokemoncobbled.client.gui.blitk
import com.cablemc.pokemoncobbled.client.gui.summary.mock.PokemonMove
import com.cablemc.pokemoncobbled.client.gui.summary.widgets.SoundlessWidget
import com.cablemc.pokemoncobbled.client.gui.summary.widgets.type.SingleTypeWidget
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.network.chat.TextComponent
import net.minecraft.resources.ResourceLocation

class MoveWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    val move: PokemonMove,
    infoX: Int, infoY: Int
): SoundlessWidget(pX, pY, pWidth, pHeight, TextComponent(move.name)) {

    companion object {
        private val moveResource = ResourceLocation(PokemonCobbled.MODID, "ui/summary/summary_moves_slot.png")
        private val movePpResource = ResourceLocation(PokemonCobbled.MODID, "ui/summary/summary_moves_overlay_pp.png")
        private const val ppWidthDiff = 2
        private const val ppHeight = 7
        private const val ppHeightDiff = 22
    }

    private val typeWidget = SingleTypeWidget(x, y, 18, 18, move.type)
    private val moveInfoWidget = MoveInfoWidget(x, y, width, height, move, infoX, infoY)

    override fun render(pMatrixStack: PoseStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        isHovered = pMouseX >= x && pMouseY >= y && pMouseX < x + width && pMouseY < y + height
        // Rendering Move Texture
        blitk(
            pMatrixStack,
            moveResource,
            x, y, height, width
        )

//        // Rendering PP Texture
//        RenderSystem.setShaderTexture(0, movePpResource)
//        RenderSystem.enableDepthTest()
//        blit(pMatrixStack, x + ppWidthDiff, y + ppHeightDiff, 0F, 0F, ((width - ppWidthDiff * 2) * getPpAsPercentage(move)).toInt(), ppHeight, width - ppWidthDiff * 2, ppHeight)

        // Render remaining PP Text
        //GuiComponent.drawCenteredString(pMatrixStack, Minecraft.getInstance().font, "${move.curPp} / ${move.maxPp}", x + width / 2, y + 22, 0xFFFFFF)

        // Render Type Icon
        //typeWidget.render(pMatrixStack, pMouseX,pMouseY, pPartialTicks)

        // Render Move Name
        //GuiComponent.drawCenteredString(pMatrixStack, Minecraft.getInstance().font, move.name, x + 85, y + 7, 0x5A5A5A)

        // Render Move Info
//        if(isHovered()) {
//            moveInfoWidget.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks)
//        }
    }

    // Get the remaining PP as percentage
    private fun getPpAsPercentage(move: PokemonMove): Double {
        return move.curPp.toDouble() / move.maxPp.toDouble()
    }
}