package com.cablemc.pokemoncobbled.client.gui.summary.widgets.pages.moves

import com.cablemc.pokemoncobbled.client.gui.ColourLibrary
import com.cablemc.pokemoncobbled.client.gui.Fonts
import com.cablemc.pokemoncobbled.client.gui.MultiLineLabelK
import com.cablemc.pokemoncobbled.client.gui.drawText
import com.cablemc.pokemoncobbled.client.gui.summary.widgets.SoundlessWidget
import com.cablemc.pokemoncobbled.common.api.moves.Move
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.network.chat.TextComponent
import net.minecraft.network.chat.TranslatableComponent
import java.math.RoundingMode
import java.text.DecimalFormat

class MoveInfoWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    val move: Move,
    private val infoX: Int, private val infoY: Int
): SoundlessWidget(pX, pY, pWidth, pHeight, TextComponent(move.name),) {

    companion object {
        private val powerText = TranslatableComponent("pokemoncobbled.ui.power")
        private val accuracyText = TranslatableComponent("pokemoncobbled.ui.accuracy")
        private val effectText = TranslatableComponent("pokemoncobbled.ui.effect")

        private const val MOVE_INFO_SPACING = 12.25F

        private val decimalFormat = DecimalFormat("#.##").also {
            it.roundingMode = RoundingMode.CEILING
        }
    }

    override fun render(pMatrixStack: PoseStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        pMatrixStack.pushPose()
        pMatrixStack.scale(0.9F, 0.9F, 1F)
        drawText(
            poseStack = pMatrixStack, font = Fonts.OSWALD_SMALL,
            text = powerText.copy().append(TextComponent(": ${move.power.toInt()}")),
            x = infoX / 0.9F, y = infoY / 0.9F,
            colour = ColourLibrary.WHITE, shadow = false
        )
        drawText(
            poseStack = pMatrixStack, font = Fonts.OSWALD_SMALL,
            text = accuracyText.copy().append(TextComponent(": ${format(move.accuracy)}")),
            x = infoX / 0.9F, y = infoY / 0.9F + MOVE_INFO_SPACING,
            colour = ColourLibrary.WHITE, shadow = false
        )
        drawText(
            poseStack = pMatrixStack, font = Fonts.OSWALD_SMALL,
            text = effectText.copy().append(TextComponent(": ${format(move.effectChance)}")),
            x = infoX / 0.9F, y = infoY / 0.9F + MOVE_INFO_SPACING * 2,
            colour = ColourLibrary.WHITE, shadow = false
        )
        /**
         * static MultiLineLabel create(Font p_94346_, FormattedText p_94347_, int p_94348_, int p_94349_)
         *                              ^ Font          ^ Component             ^ x size        ^ y spacing
         */
        //MultiLineLabel.create(Minecraft.getInstance().font, move.description, 30, 10).renderLeftAlignedNoShadow(pMatrixStack, 30, 30, 30, 30)
        MultiLineLabelK.create(
            component = move.description,
            width = 175,
            maxLines = 4,
            font = Fonts.OSWALD_SMALL
        ).renderLeftAligned(
            poseStack = pMatrixStack,
            x = (infoX + 60.0) / 0.9, y = (infoY - 1.15) / 0.9,
            ySpacing = 8.0 / 0.9,
            colour = ColourLibrary.WHITE, shadow = false
        )
        pMatrixStack.popPose()
    }

    private fun format(input: Double): String {
        if(input == -1.0)
            return "—"
        return "${decimalFormat.format(input)}%"
    }
}