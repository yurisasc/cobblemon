/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.gui.pc

import com.cablemc.pokemod.common.api.gui.blitk
import com.cablemc.pokemod.common.client.storage.ClientPC
import com.cablemc.pokemod.common.util.pokemodResource
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

class PCTrashWidget(
    x: Int, y: Int,
    private val parent: PCWidget,
    private val pc: ClientPC,
    onPress: PressAction
) : ButtonWidget(x, y, WIDTH, HEIGHT, Text.literal("PCTrash"), onPress) {

    companion object {
        private const val WIDTH = 13
        private const val HEIGHT = 14

        val trashIcon = pokemodResource("ui/pc/pc_trash.png")
        val trashIconHovered = pokemodResource("ui/pc/pc_trash_hovered.png")
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        if (!parent.canDeleteSelected()) {
            return
        }

        val isHovered = mouseX in x..(x + WIDTH) && mouseY in y..(y + HEIGHT)

        val icon = if (isHovered) trashIconHovered else trashIcon

        blitk(
            matrixStack = matrices,
            x = x,
            y = y,
            width = WIDTH,
            height = HEIGHT,
            texture = icon
        )
    }
}