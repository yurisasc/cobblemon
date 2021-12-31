package com.cablemc.pokemoncobbled.client.gui.summary.widgets.pages.moves

import com.cablemc.pokemoncobbled.client.gui.ColourLibrary
import com.cablemc.pokemoncobbled.client.gui.Fonts
import com.cablemc.pokemoncobbled.client.gui.drawText
import com.cablemc.pokemoncobbled.client.gui.summary.mock.PokemonMove
import com.cablemc.pokemoncobbled.client.gui.summary.widgets.SoundlessWidget
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.TextComponent
import net.minecraft.network.chat.TranslatableComponent

class MoveInfoWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    val move: PokemonMove,
    private val infoX: Int, private val infoY: Int
): SoundlessWidget(pX, pY, pWidth, pHeight, TextComponent(move.name),) {

    companion object {
        private val powerText = TranslatableComponent("pokemoncobbled.ui.power")
        private val accuracyText = TranslatableComponent("pokemoncobbled.ui.accuracy")
        private val effectText = TranslatableComponent("pokemoncobbled.ui.effect")
    }

    private val font = Minecraft.getInstance().font

    override fun render(pMatrixStack: PoseStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        //GuiComponent.drawString(pMatrixStack, font, powerText.copy().append(TextComponent(": ${move.power.toInt()}")), infoX, infoY, 0xFFFFFF)
        pMatrixStack.pushPose()
        pMatrixStack.scale(0.9F, 0.9F, 1F)
        drawText(
            poseStack = pMatrixStack, font = Fonts.OSWALD_SMALL,
            text = powerText.copy().append(TextComponent(": ${move.power.toInt()}")),
            x = infoX / 0.9F, y = infoY / 0.9F,
            colour = ColourLibrary.WHITE, shadow = false
        )
        pMatrixStack.popPose()
    }
}