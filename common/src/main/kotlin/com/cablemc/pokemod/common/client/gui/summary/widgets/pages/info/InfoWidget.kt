/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.gui.summary.widgets.pages.info

import com.cablemc.pokemod.common.api.gui.ColourLibrary
import com.cablemc.pokemod.common.api.gui.MultiLineLabelK
import com.cablemc.pokemod.common.api.text.plus
import com.cablemc.pokemod.common.api.text.underline
import com.cablemc.pokemod.common.client.gui.summary.widgets.SoundlessWidget
import com.cablemc.pokemod.common.client.render.drawScaledText
import com.cablemc.pokemod.common.pokemon.Pokemon
import com.cablemc.pokemod.common.util.lang
import com.cablemc.pokemod.common.util.pokemodResource
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

class InfoWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    private val pokemon: Pokemon
): SoundlessWidget(pX, pY, pWidth, pHeight, Text.literal("InfoWidget")) {

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        // Rendering Info Texture
        RenderSystem.setShaderTexture(0, infoBaseResource)
        RenderSystem.enableDepthTest()
        drawTexture(matrices, x, y, 0F, 0F, width, height, width, height)

        val left = x + 10
        val valueLeft = left + 60
        val top = y + 35

        val lineSeparation = 14

        drawScaledText(
            matrixStack = matrices,
            text = lang("ui.info.species").underline(),
            x = left,
            y = top
        )

        drawScaledText(
            matrixStack = matrices,
            text = pokemon.species.translatedName,
            x = valueLeft,
            y = top
        )

        var line = 1

        drawScaledText(
            matrixStack = matrices,
            text = lang("ui.info.type").underline(),
            x = left,
            y = top + lineSeparation * line
        )

        val typeValue = pokemon.types.map { it.displayName }.reduce { acc, next -> acc.plus("/").plus(next) }

        drawScaledText(
            matrixStack = matrices,
            text = typeValue,
            x = valueLeft,
            y = top + lineSeparation * line
        )

        line++

        drawScaledText(
            matrixStack = matrices,
            text = lang("ui.info.nature").underline(),
            x = left,
            y = top + lineSeparation * line
        )

        drawScaledText(
            matrixStack = matrices,
            text = pokemon.nature.displayName,
            x = valueLeft,
            y = top + lineSeparation * line
        )

//        line++
//
//        drawScaledText(
//            matrixStack = matrices,
//            text = lang("ui.info.originaltrainer").underline(),
//            x = left,
//            y = top + lineSeparation * line
//        )
//
//        drawScaledText(
//            matrixStack = matrices,
//            text = "me".text(),
//            x = valueLeft,
//            y = top + lineSeparation * line
//        )

        line++
        line++

        drawScaledText(
            matrixStack = matrices,
            text = lang("ui.info.ability").underline(),
            x = left,
            y = top + lineSeparation * line
        )

        drawScaledText(
            matrixStack = matrices,
            text = pokemon.ability.displayName,
            x = valueLeft,
            y = top + lineSeparation * line
        )

        line++

        MultiLineLabelK.create(
            component = pokemon.ability.description,
            width = 160,
            maxLines = 4
        ).renderLeftAligned(
            poseStack = matrices,
            x = left,
            y = top + lineSeparation * line,
            ySpacing = 12,
            colour = ColourLibrary.WHITE,
            shadow = false
        )
    }

    companion object {
        private val infoBaseResource = pokemodResource("ui/summary/summary_info.png")
    }
}