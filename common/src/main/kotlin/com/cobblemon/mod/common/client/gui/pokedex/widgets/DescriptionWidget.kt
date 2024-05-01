/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.pokedex.widgets

import com.cobblemon.mod.common.api.gui.ColourLibrary
import com.cobblemon.mod.common.api.gui.MultiLineLabelK
import com.cobblemon.mod.common.api.pokedex.SpeciesPokedexEntry
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.client.gui.ScrollingWidget
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.pokemon.Species
import com.cobblemon.mod.common.util.asTranslated
import com.cobblemon.mod.common.util.lang
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.MutableText
import net.minecraft.text.OrderedText
import net.minecraft.text.Style
import net.minecraft.text.Text
import java.util.stream.Collectors

class DescriptionWidget(val pX: Int, val pY: Int): ScrollingWidget<DescriptionWidget.TextSlot>(
    left = pX,
    top = pY,
    width = PokedexGUIConstants.POKEMON_DESCRIPTION_WIDTH,
    height = PokedexGUIConstants.POKEMON_DESCRIPTION_HEIGHT
) {
    override fun addEntry(entry: TextSlot): Int {
        return super.addEntry(entry)
    }

    fun setText(text: Collection<String>){
        clearEntries()
        text.forEach {
            MinecraftClient.getInstance().textRenderer.textHandler.wrapLines(it.text(), (width - PokedexGUIConstants.SCROLL_BAR_WIDTH - 5), Style.EMPTY).stream()
                .map { it.string }
                .forEach { addEntry(TextSlot(it)) }
        }
    }

    override fun getScrollbarPositionX(): Int {
        return left + width - scrollBarWidth
    }

    class TextSlot(val text : String) : Slot<TextSlot>() {
        override fun render(
            context: DrawContext,
            index: Int,
            y: Int,
            x: Int,
            entryWidth: Int,
            entryHeight: Int,
            mouseX: Int,
            mouseY: Int,
            hovered: Boolean,
            tickDelta: Float
        ) {
            val matrices = context.matrices

            val textScale = 1F

            matrices.push()
            MultiLineLabelK.create(
                component = text.text(),
                width = (entryWidth - PokedexGUIConstants.SCROLL_BAR_WIDTH) / textScale,
                maxLines = 20
            ).renderLeftAligned(
                context = context,
                x = x,
                y = y,
                ySpacing = 10 / textScale,
                colour = ColourLibrary.WHITE,
                shadow = true
            )
            matrices.pop()
        }

        override fun getNarration(): Text {
            return Text.of(text)
        }
    }
}