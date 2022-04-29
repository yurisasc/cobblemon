package com.cablemc.pokemoncobbled.common.api.gui

import com.mojang.blaze3d.vertex.MatrixStack
import net.minecraft.client.Minecraft
import net.minecraft.text.Text
import net.minecraft.network.chat.FormattedText
import net.minecraft.network.chat.Style
import net.minecraft.util.Identifier
import java.util.stream.Collectors

class MultiLineLabelK(
    private val comps: List<TextWithWidth>,
    private val font: Identifier? = null
) {

    companion object {
        private val mcFont = MinecraftClient.getInstance().font

        fun create(component: Text, width: Number, maxLines: Number) = create(component, width, maxLines, null)

        fun create(component: Text, width: Number, maxLines: Number, font: Identifier?): MultiLineLabelK {
            return MultiLineLabelK(
                mcFont.splitter.splitLines(component, width.toInt(), Style.EMPTY).stream()
                    .limit(maxLines.toLong())
                    .map {
                    TextWithWidth(it, mcFont.width(it))
                }.collect(Collectors.toList()),
                font = font
            )
        }
    }

    fun renderLeftAligned(
        poseStack: MatrixStack,
        x: Number, y: Number,
        ySpacing: Number,
        colour: Int,
        shadow: Boolean = true
    ) {
        comps.forEachIndexed { index, textWithWidth ->
            drawString(
                poseStack = poseStack,
                x = x, y = y.toFloat() + ySpacing.toFloat() * index,
                colour = colour,
                shadow = shadow,
                text = textWithWidth.text.string,
                font = font
            )
        }
    }

    class TextWithWidth internal constructor(val text: FormattedText, val width: Int)
}