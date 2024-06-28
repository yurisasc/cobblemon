/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.npc.widgets

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.gui.drawCenteredText
import com.cobblemon.mod.common.api.text.text
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.network.chat.MutableComponent
import net.minecraft.text.PlainTextContent.Literal
import net.minecraft.resources.ResourceLocation

class SimpleNPCTextInputWidget(
    getter: () -> String,
    val texture: ResourceLocation,
    private val setter: (String) -> Unit,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    maxLength: Int = 100,
    val wrap: Boolean = false
) : TextFieldWidget(
    Minecraft.getInstance().textRenderer,
    x,
    y,
    width,
    height,
    "input".text()
) {

    init {
        setMaxLength(maxLength)
        isFocused = true
        text = getter()
        this.setChangedListener { setter(it) }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return if (mouseX.toInt() in x..(x + width) && mouseY.toInt() in y..(y + height)) {
            isFocused = true
            true
        } else {
            false
        }
    }

    override fun renderWidget(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        if (cursor != text.length) {
            setCursorToEnd(false)
        }

        blitk(
            matrixStack = context.matrices,
            x = x,
            y = y,
            width = width,
            height = height,
            texture = texture
        )

        context.matrices.push()
        val scale = 0.8F
        context.matrices.scale(scale, scale, 1F)
        if (wrap) {
            val wrappedLines = Minecraft.getInstance().textRenderer.wrapLines(MutableComponent.of(Literal(text)), ((width - 8) / scale).toInt())
            for ((index, line) in wrappedLines.withIndex()) {
                context.drawText(
                    Minecraft.getInstance().textRenderer,
                    line,
                    ((x + 4) / scale).toInt(),
                    ((y + index * 10 + 5) / scale).toInt(),
                    0xFFFFFF,
                    false
                )
            }
        } else {
            drawCenteredText(
                context = context,
                text = text.text(),
                x = (x + width / 2F) / scale,
                y = (y + height / 2 - 4) / scale,
                shadow = true,
                colour = 0xFFFFFF
            )
        }
        context.matrices.pop()
    }
}