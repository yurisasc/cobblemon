package com.cablemc.pokemoncobbled.client.gui.summary.widgets.pages.moves

import com.cablemc.pokemoncobbled.client.gui.Fonts
import com.cablemc.pokemoncobbled.client.gui.blitk
import com.cablemc.pokemoncobbled.client.gui.drawCenteredText
import com.cablemc.pokemoncobbled.client.gui.summary.mock.PokemonMove
import com.cablemc.pokemoncobbled.client.gui.summary.widgets.SoundlessWidget
import com.cablemc.pokemoncobbled.client.gui.summary.widgets.type.SingleTypeWidget
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
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
        private const val PP_WIDTH_DIFF = 3
        private const val PP_HEIGHT = 10
        private const val PP_HEIGHT_DIFF = 20
        private const val MOVE_BUTTON_WIDTH = 15
        private const val MOVE_BUTTON_HEIGHT = 12
        private const val TYPE_WIDGET_Y_OFFSET = 2
        private const val MOVE_UP_BUTTON_Y_OFFSET = 2
        private const val MOVE_DOWN_BUTTON_Y_OFFSET = 14
        private const val MOVE_NAME_COLOUR = 0x0A0A0A
    }

    private val typeWidget = SingleTypeWidget(x + 3, y + TYPE_WIDGET_Y_OFFSET, 18, 18, move.type)
    private val moveInfoWidget = MoveInfoWidget(x, y, width, height, move, infoX, infoY)
    private val moveUpButton = MovesMoveButton(x - 15, y + MOVE_UP_BUTTON_Y_OFFSET, MOVE_BUTTON_WIDTH, MOVE_BUTTON_HEIGHT, textureWidth, textureHeight, true) {
        movesWidget.moveMove(this, true)
    }.apply {
        addWidget(this)
    }
    private val moveDownButton = MovesMoveButton(x - 15, y + MOVE_DOWN_BUTTON_Y_OFFSET, MOVE_BUTTON_WIDTH, MOVE_BUTTON_HEIGHT, textureWidth, textureHeight, false) {
        movesWidget.moveMove(this, false)
    }.apply {
        addWidget(this)
    }

    fun update() {
        typeWidget.y = y + TYPE_WIDGET_Y_OFFSET
        moveUpButton.y = y + MOVE_UP_BUTTON_Y_OFFSET
        moveDownButton.y = y + MOVE_DOWN_BUTTON_Y_OFFSET
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
        blit(pMatrixStack, ((x + PP_WIDTH_DIFF) / 1F).roundToInt(), ((y + PP_HEIGHT_DIFF) / 0.945F).roundToInt(), 0F, 0F, ((width - PP_WIDTH_DIFF * 2) * getPpAsPercentage(move)).toInt() + 2, PP_HEIGHT, width - 4, PP_HEIGHT)
        pMatrixStack.popPose()

        // Render remaining PP Text
        pMatrixStack.pushPose()
        pMatrixStack.scale(0.6F, 0.6F, 0.6F)
        drawCenteredText(pMatrixStack, Fonts.OSWALD_SMALL, TextComponent("${move.curPp} / ${move.maxPp}"), ((x + width / 2) / 0.6) + 3, ((y + 23) / 0.6) + 1, 0xFFFFFF, false)
        pMatrixStack.popPose()

        // Render Type Icon
        typeWidget.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks)

        // Render Move Name
        pMatrixStack.pushPose()
        pMatrixStack.scale(0.5F, 0.5F, 0.5F)
        drawCenteredText(pMatrixStack, Fonts.OSWALD, TextComponent(move.name), (x + 85) / 0.5F, y / 0.5F + 14, MOVE_NAME_COLOUR, false)
        pMatrixStack.popPose()

        // Render Move Info
        if(isHovered()) {
            moveInfoWidget.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks)
        }

        // Render Move Move Button
        moveUpButton.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks)
        moveDownButton.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks)
    }

    // Get the remaining PP as percentage
    private fun getPpAsPercentage(move: PokemonMove): Double {
        return move.curPp.toDouble() / move.maxPp.toDouble()
    }
}