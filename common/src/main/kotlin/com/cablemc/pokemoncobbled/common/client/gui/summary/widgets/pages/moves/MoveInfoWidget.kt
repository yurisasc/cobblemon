package com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.pages.moves

import com.cablemc.pokemoncobbled.common.api.gui.ColourLibrary
import com.cablemc.pokemoncobbled.common.api.gui.MultiLineLabelK
import com.cablemc.pokemoncobbled.common.api.moves.Move
import com.cablemc.pokemoncobbled.common.api.text.text
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.SoundlessWidget
import com.cablemc.pokemoncobbled.common.client.render.drawScaledText
import java.math.RoundingMode
import java.text.DecimalFormat
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText
import net.minecraft.text.TranslatableText

class MoveInfoWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    val move: Move,
    private val infoX: Int, private val infoY: Int
): SoundlessWidget(pX, pY, pWidth, pHeight, LiteralText(move.name)) {

    companion object {
        private val powerText = TranslatableText("pokemoncobbled.ui.power")
        private val accuracyText = TranslatableText("pokemoncobbled.ui.accuracy")
        private val effectText = TranslatableText("pokemoncobbled.ui.effect")

        private const val MOVE_INFO_SPACING = 20.25F
        private const val SCALE = 0.60F

        private val decimalFormat = DecimalFormat("#.##").also {
            it.roundingMode = RoundingMode.CEILING
        }
    }

    override fun render(pMatrixStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        pMatrixStack.push()
        pMatrixStack.scale(SCALE, SCALE, 1F)

        val moveInfoValueOffset = 84

        drawScaledText(
            matrixStack = pMatrixStack,
            text = powerText,
            x = infoX / SCALE + 1, y = infoY / SCALE + 2.25,
            colour = ColourLibrary.WHITE, shadow = false
        )
        drawScaledText(
            matrixStack = pMatrixStack,
            text = move.power.toInt().toString().text(),
            centered = true,
            x = infoX / SCALE + moveInfoValueOffset, y = infoY / SCALE + 2.25,
            colour = ColourLibrary.WHITE, shadow = false
        )

        drawScaledText(
            matrixStack = pMatrixStack,
            text = accuracyText,
            x = infoX / SCALE + 1, y = infoY / SCALE + MOVE_INFO_SPACING + 0.25,
            colour = ColourLibrary.WHITE, shadow = false
        )
        drawScaledText(
            matrixStack = pMatrixStack,
            text = format(move.accuracy).text(),
            centered = true,
            x = infoX / SCALE + moveInfoValueOffset, y = infoY / SCALE + MOVE_INFO_SPACING + 0.25,
            colour = ColourLibrary.WHITE, shadow = false
        )

        drawScaledText(
            matrixStack = pMatrixStack,
            text = effectText,
            x = infoX / SCALE + 1, y = infoY / SCALE + MOVE_INFO_SPACING * 2 - 1.0,
            colour = ColourLibrary.WHITE, shadow = false
        )
        drawScaledText(
            matrixStack = pMatrixStack,
            text = format(move.effectChance).text(),
            centered = true,
            x = infoX / SCALE + 1 + moveInfoValueOffset, y = infoY / SCALE + MOVE_INFO_SPACING * 2 - 1.0,
            colour = ColourLibrary.WHITE, shadow = false
        )

        MultiLineLabelK.create(
            component = move.description,
            width = 170,
            maxLines = 4
        ).renderLeftAligned(
            poseStack = pMatrixStack,
            x = (infoX + 67.0) / SCALE, y = (infoY) / SCALE,
            ySpacing = 8.0 / SCALE,
            colour = ColourLibrary.WHITE, shadow = false
        )
        pMatrixStack.pop()
    }

    private fun format(input: Double): String {
        if (input == -1.0)
            return "—"
        return "${decimalFormat.format(input)}%"
    }
}