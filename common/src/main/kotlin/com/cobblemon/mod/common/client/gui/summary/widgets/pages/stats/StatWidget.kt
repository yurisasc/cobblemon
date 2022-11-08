/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.summary.widgets.pages.stats

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.pokemon.stats.Stat
import com.cobblemon.mod.common.api.text.green
import com.cobblemon.mod.common.api.text.lightPurple
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.api.text.underline
import com.cobblemon.mod.common.api.text.yellow
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.lang
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.stat.Stats
import net.minecraft.text.MutableText
import net.minecraft.text.Text

class StatWidget(
    val pokemon: Pokemon,
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int
): SoundlessWidget(pX, pY, pWidth, pHeight, Text.literal("StatWidget")) {

    companion object {
        private val statBaseResource = cobblemonResource("ui/summary/summary_stats.png")
    }

    override fun render(pMatrixStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        // Rendering Stat Texture
        RenderSystem.setShaderTexture(0, statBaseResource)
        RenderSystem.enableDepthTest()
        drawTexture(pMatrixStack, x, y, 0F, 0F, width, height, width, height)

        val labelLeft = x + 8
        val statLeft = labelLeft + 90
        val ivLeft = statLeft + 35
        val evLeft = ivLeft + 35

        val top = y + 30
        val left = x + 8
        val valueLeft = left + 60

        val lineSeparation = 14

        drawScaledText(
            matrixStack = pMatrixStack,
            text = lang("ui.stats.stat").underline(),
            x = statLeft,
            y = top
        )

        var statIndex = 1

        for (stat in Cobblemon.statProvider.ofType(Stat.Type.PERMANENT)) {
            drawScaledText(
                matrixStack = pMatrixStack,
                text = stat.displayName.copy(),
                x = labelLeft,
                y = top + statIndex * lineSeparation
            )

            statIndex++
        }

        statIndex = 1

        for (stat in Cobblemon.statProvider.ofType(Stat.Type.PERMANENT)) {
            drawScaledText(
                matrixStack = pMatrixStack,
                text = pokemon.getStat(stat).toString().text(),
                x = statLeft,
                y = top + statIndex * lineSeparation
            )

            statIndex++
        }

        drawScaledText(
            matrixStack = pMatrixStack,
            text = lang("ui.stats.ivs").underline(),
            x = ivLeft,
            y = top
        )

        statIndex = 1
        for (stat in Cobblemon.statProvider.ofType(Stat.Type.PERMANENT)) {
            drawScaledText(
                matrixStack = pMatrixStack,
                text = pokemon.ivs[stat].toString().text(),
                x = ivLeft,
                y = top + statIndex * lineSeparation
            )

            statIndex++
        }

        var line = Cobblemon.statProvider.ofType(Stat.Type.PERMANENT).size + 1
        line++

        drawScaledText(
            matrixStack = pMatrixStack,
            text = lang("ui.stats.friendship").underline(),
            x = left,
            y = top + lineSeparation * line
        )

        drawScaledText(
            matrixStack = pMatrixStack,
            text = pokemon.friendship.let {
                 if (it < 80) {
                     "$it".red()
                 } else if (it < 160) {
                     "$it".yellow()
                 } else if (it < 240) {
                     "$it".green()
                 } else {
                     "$it".lightPurple()
                 }
            },
            x = valueLeft,
            y = top + lineSeparation * line
        )
    }
}