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
import kotlin.math.roundToInt

class MoveWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    val move: PokemonMove,
    infoX: Int, infoY: Int,
    private val textureWidth: Int, private val textureHeight: Int,
    private val movesWidget: MovesWidget
): SoundlessWidget(pX, pY, pWidth, pHeight, TextComponent(move.name)) {

    companion object {
        private val moveResource = ResourceLocation(PokemonCobbled.MODID, "ui/summary/summary_moves_slot.png")
        private val movePpResource = ResourceLocation(PokemonCobbled.MODID, "ui/summary/summary_moves_overlay_pp.png")
        private const val ppWidthDiff = 3
        private const val ppHeight = 10
        private const val ppHeightDiff = 20
        private const val MOVE_BUTTON_WIDTH = 15
        private const val MOVE_BUTTON_HEIGHT = 12
    }

    private val typeWidget = SingleTypeWidget(x + 3, y + 2, 18, 18, move.type)
    private val moveInfoWidget = MoveInfoWidget(x, y, width, height, move, infoX, infoY)
    private val moveUpButton = MovesMoveButton(x - 15, y + 2, MOVE_BUTTON_WIDTH, MOVE_BUTTON_HEIGHT) {
        movesWidget.moveMove(this, true)
    }.apply {
        addWidget(this)
    }
    private val moveDownButton = MovesMoveButton(x, y, width, height) {
        movesWidget.moveMove(this, false)
    }.apply {
        addWidget(this)
    }

    override fun render(pMatrixStack: PoseStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        isHovered = pMouseX >= x && pMouseY >= y && pMouseX < x + width && pMouseY < y + height
        // Rendering Move Texture
        blitk(
            pMatrixStack,
            moveResource,
            x - 19, y - 27, textureHeight, textureWidth
        )

        // Rendering PP Texture
        RenderSystem.setShaderTexture(0, movePpResource)
        RenderSystem.enableDepthTest()
        pMatrixStack.pushPose()
        pMatrixStack.scale(1F, 0.945F, 1F)
        blit(pMatrixStack, ((x + ppWidthDiff) / 1F).roundToInt(), ((y + ppHeightDiff) / 0.945F).roundToInt(), 0F, 0F, ((width - ppWidthDiff * 2) * getPpAsPercentage(move)).toInt() + 2, ppHeight, width - 4, ppHeight)
        pMatrixStack.popPose()

        // Render remaining PP Text
        pMatrixStack.pushPose()
        pMatrixStack.scale(0.6F, 0.6F, 0.6F)
        GuiComponent.drawCenteredString(pMatrixStack, Minecraft.getInstance().font, "${move.curPp} / ${move.maxPp}", ((x + width / 2) / 0.6).roundToInt() + 3, ((y + 23) / 0.6).roundToInt() + 1, 0xFFFFFF)
        pMatrixStack.popPose()

        // Render Type Icon
        typeWidget.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks)

        // Render Move Name
        GuiComponent.drawCenteredString(pMatrixStack, Minecraft.getInstance().font, move.name, x + 85, y + 7, 0x5A5A5A)

        // Render Move Info
        if(isHovered()) {
            moveInfoWidget.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks)
        }

        // Render Move Move Button
        moveUpButton.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks)
        //moveDownButton.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks)
    }

    // Get the remaining PP as percentage
    private fun getPpAsPercentage(move: PokemonMove): Double {
        return move.curPp.toDouble() / move.maxPp.toDouble()
    }
}