package com.cablemc.pokemoncobbled.common.api.gui

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FormattedText
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import java.util.stream.Collectors

class MultiLineLabelK(
    private val comps: List<TextWithWidth>,
    private val font: ResourceLocation? = null
) {

    companion object {
        private val mcFont = Minecraft.getInstance().font

        fun create(component: Component, width: Number, maxLines: Number) = create(component, width, maxLines, null)

        fun create(component: Component, width: Number, maxLines: Number, font: ResourceLocation?): MultiLineLabelK {
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
        poseStack: PoseStack,
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