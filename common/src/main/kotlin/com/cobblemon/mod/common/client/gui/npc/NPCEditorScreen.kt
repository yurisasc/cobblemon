/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.npc

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.client.gui.dialogue.widgets.DialogueTextInputWidget
import com.cobblemon.mod.common.client.gui.npc.widgets.NPCRenderWidget
import com.cobblemon.mod.common.client.gui.npc.widgets.SimpleNPCTextInputWidget
import com.cobblemon.mod.common.net.messages.client.npc.dto.NPCConfigurationDTO
import com.cobblemon.mod.common.util.asTranslated
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen

class NPCEditorScreen(
    val npcId: Int,
    val dto: NPCConfigurationDTO
) : Screen("gui.npc_editor.title".asTranslated()) {
    companion object {
        const val BASE_WIDTH = 331
        const val BASE_HEIGHT = 161

        private val baseResource = cobblemonResource("textures/gui/npc/editor_base.png")
    }

    val middleX: Int
        get() = this.client!!.window.scaledWidth / 2
    val middleY: Int
        get() = this.client!!.window.scaledHeight / 2

    val leftX: Int
        get() = middleX - BASE_WIDTH / 2
    val topY: Int
        get() = middleY - BASE_HEIGHT / 2

    override fun init() {
        super.init()
        addDrawable(NPCRenderWidget(leftX + 6, topY + 32, dto.npcClass, dto.aspects))
        addDrawableChild(SimpleNPCTextInputWidget(
            texture = cobblemonResource("textures/gui/npc/basic_text_input.png"),
            getter = { dto.npcName.string },
            setter = { dto.npcName = it.text() },
            x = leftX + 79,
            y = topY + 14,
            width = 130,
            height = 22
        ))
        addDrawableChild(SimpleNPCTextInputWidget(
            texture = cobblemonResource("textures/gui/npc/aspects_text_input.png"),
            getter = { dto.aspects.joinToString() },
            setter = {
                dto.aspects.clear()
                dto.aspects.addAll(it.split(",").map { it.trim() })
            },
            x = leftX + 79,
            y = topY + 36,
            width = 130,
            height = 40,
            maxLength = 75,
            wrap = true
        ))
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        blitk(
            matrixStack = context.matrices,
            texture = baseResource,
            x = leftX,
            y = topY,
            height = BASE_HEIGHT,
            width = BASE_WIDTH
        )
        super.render(context, mouseX, mouseY, delta)
    }
}