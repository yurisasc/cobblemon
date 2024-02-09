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
import com.cobblemon.mod.common.client.gui.dialogue.DialogueScreen
import com.cobblemon.mod.common.net.messages.client.dialogue.dto.DialogueInputDTO
import com.cobblemon.mod.common.net.messages.server.dialogue.InputToDialoguePacket
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.util.InputUtil
import net.minecraft.text.LiteralTextContent
import net.minecraft.text.MutableText
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW

class SimpleNPCTextInputWidget(
    getter: () -> String,
    val texture: Identifier,
    private val setter: (String) -> Unit,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    maxLength: Int = 100,
    val wrap: Boolean = false
) : TextFieldWidget(
    MinecraftClient.getInstance().textRenderer,
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
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return if (mouseX.toInt() in x..(x + width) && mouseY.toInt() in y..(y + height)) {
            isFocused = true
            true
        } else {
            false
        }
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (cursor != text.length) {
            setCursorToEnd()
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
            val wrappedLines = MinecraftClient.getInstance().textRenderer.wrapLines(MutableText.of(LiteralTextContent(text)), ((width - 8) / scale).toInt())
            for ((index, line) in wrappedLines.withIndex()) {
                context.drawText(
                    MinecraftClient.getInstance().textRenderer,
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
                text = (if (isFocused) "$text|" else text).text(),
                x = (x + width / 2F) / scale,
                y = (y + height / 2 - 4) / scale,
                shadow = true,
                colour = 0xFFFFFF
            )
        }
        context.matrices.pop()
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (keyCode == InputUtil.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            setter(text.trim())
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }
}