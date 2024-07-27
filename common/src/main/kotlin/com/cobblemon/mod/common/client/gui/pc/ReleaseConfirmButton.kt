/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.pc

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.lang
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.navigation.GuiNavigation
import net.minecraft.client.gui.navigation.GuiNavigationPath
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.sound.SoundManager
import net.minecraft.text.Text

class ReleaseConfirmButton(
    x: Int, y: Int,
    private val parent: StorageWidget,
    private val subKey: String,
    onPress: PressAction
) : ButtonWidget(x, y, WIDTH, HEIGHT, Text.literal("ReleaseConfirm"), onPress, DEFAULT_NARRATION_SUPPLIER) {

    companion object {
        private const val WIDTH = 30
        private const val HEIGHT = 13

        private val buttonResource = cobblemonResource("textures/gui/pc/pc_release_button_confirm.png")
    }

    val isVisible: Boolean
        get() = parent.canDeleteSelected() && !parent.displayConfirmRelease

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (isVisible) {
            blitk(
                matrixStack = context.matrices,
                texture = buttonResource,
                x = x,
                y = y,
                width = WIDTH,
                height = HEIGHT,
                vOffset = if (isHovered || isFocused) HEIGHT else 0,
                textureHeight = HEIGHT * 2
            )

            drawScaledText(
                context = context,
                font = CobblemonResources.DEFAULT_LARGE,
                text = lang(subKey).bold(),
                x = x + (WIDTH / 2),
                y = y + 2,
                centered = true,
                shadow = true
            )
        }
    }

    override fun playDownSound(soundManager: SoundManager) {
    }

    override fun getNavigationPath(navigation: GuiNavigation?): GuiNavigationPath? {
        // prevent navigation to this element if it's not visible
        return super.getNavigationPath(navigation).takeIf { isVisible }
    }
}