/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.dialogue.widgets

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.gui.drawCenteredText
import com.cobblemon.mod.common.client.gui.dialogue.DialogueScreen
import com.cobblemon.mod.common.net.messages.server.dialogue.InputToDialoguePacket
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.PressableWidget
import net.minecraft.text.MutableText
import net.minecraft.util.Identifier

class DialogueOptionWidget(
    val dialogueScreen: DialogueScreen,
    val text: MutableText,
    val value: String,
    val selectable: Boolean,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    val texture: Identifier,
    val overlayTexture: Identifier
) : PressableWidget(x, y, width, height, text) {
    override fun renderButton(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        blitk(
            texture = texture,
            matrixStack = context.matrices,
            x = x,
            y = y,
            width = width,
            height = height,
            vOffset = if (selectable && (isHovered || isFocused)) 24 else 0,
            textureHeight = height * 2,
        )

        blitk(
            texture = overlayTexture,
            matrixStack = context.matrices,
            x = x,
            y = y,
            width = width,
            height = height,
            textureWidth = width,
            textureHeight = height,
        )

        drawCenteredText(
            context = context,
            text = text,
            x = x + width / 2,
            y = y + height / 2 - 4,
            shadow = true,
            colour = if (selectable) 0xFFFFFF else 0x808080
        )

    }

    override fun onPress() {
        if (selectable && !dialogueScreen.waitingForServerUpdate) {
            dialogueScreen.sendToServer(InputToDialoguePacket(dialogueScreen.dialogueDTO.dialogueInput.inputId, value))
        }
    }

    override fun appendClickableNarrations(builder: NarrationMessageBuilder?) {
    }
}