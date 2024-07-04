/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.pokedex.widgets

import com.cobblemon.mod.common.api.drop.DropTable
import com.cobblemon.mod.common.api.drop.ItemDropEntry
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.gui.ScrollingWidget
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.client.render.renderScaledGuiItemIcon
import net.minecraft.client.gui.DrawContext
import net.minecraft.registry.Registries
import net.minecraft.text.Text
import net.minecraft.util.math.ColorHelper
import net.minecraft.util.math.MathHelper

class DropsScrollingWidget(val pX: Int, val pY: Int): ScrollingWidget<DropsScrollingWidget.DropWidgetEntry>(
    width = PokedexGUIConstants.HALF_OVERLAY_WIDTH - 2,
    height = 42,
    left = pX,
    top = pY + 10,
    slotHeight = 10
) {

    var dropTable: DropTable = DropTable()

    fun dropsAvailable(): Boolean {
        return dropTable.entries.size != 0
    }

    init {
        setEntries()
    }

    fun setEntries() {
        dropTable.entries.forEach {
            if (it is ItemDropEntry) addEntry(DropWidgetEntry(it))
        }
    }

    override fun getX(): Int {
        return pX
    }

    override fun getY(): Int {
        return pY
    }

    override fun getScrollbarX(): Int {
        return left + width - scrollBarWidth
    }

    override fun getMaxScroll(): Int {
//        val contentHeight = children().size * 10
//        println("Content Height: $contentHeight") // Debug statement
//        return Math.max(0, contentHeight - height)
        return super.getMaxScroll()
    }

    override fun renderScrollbar(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val xLeft = this.scrollbarX
        val xRight = xLeft + 3

        val barHeight = this.bottom - this.y

        var yBottom = ((barHeight * barHeight).toFloat() / this.maxPosition.toFloat()).toInt()
        yBottom = MathHelper.clamp(yBottom, 32, barHeight - 8)
        var yTop = scrollAmount.toInt() * (barHeight - yBottom) / this.maxScroll + this.y
        if (yTop < this.y) {
            yTop = this.y
        }

        context.fill(xLeft + 1, this.y + 1, xRight - 1, this.bottom - 1, ColorHelper.Argb.getArgb(255, 126, 231, 229)) // background
        context.fill(xLeft, yTop + 1, xRight, yTop + yBottom - 1, ColorHelper.Argb.getArgb(255, 58, 150, 182)) // base
    }

    override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        drawScaledText(
            context = context,
            font = CobblemonResources.DEFAULT_LARGE,
            text = Text.translatable("cobblemon.ui.pokedex.info.drops").bold(),
            x = pX + 7,
            y = pY - 10,
            shadow = true
        )

        super.renderWidget(context, mouseX, mouseY, delta)
    }

    override fun renderEntry(
        context: DrawContext,
        mouseX: Int,
        mouseY: Int,
        delta: Float,
        index: Int,
        x: Int,
        y: Int,
        entryWidth: Int,
        entryHeight: Int
    ) {
        val entry =  this.getEntry(index)
        entry.render(
            context, index, y + 2, x, entryWidth, entryHeight, mouseX, mouseY,
            hoveredEntry == entry, delta
        )
    }

    override fun getEntry(index: Int): DropWidgetEntry {
        return children()[index] as DropWidgetEntry
    }

    class DropWidgetEntry(val entry: ItemDropEntry): Slot<DropWidgetEntry>() {
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
            context.matrices.push()
            context.matrices.translate(0f, 0f, 100f)
            val itemStack = Registries.ITEM.get(entry.item).defaultStack
            renderScaledGuiItemIcon(
                itemStack = itemStack,
                x = x.toDouble(),
                y = y.toDouble(),
                matrixStack = context.matrices,
                scale = PokedexGUIConstants.SCALE.toDouble()
            )
            context.matrices.push()

            val dropNumList = mutableListOf<String>()
            if (entry.quantityRange != null) {
                dropNumList.add(
                    Text.translatable(
                        "cobblemon.ui.pokedex.info.drops_range",
                        entry.quantityRange!!.min(),
                        entry.quantityRange!!.max()
                    ).string
                )
            } else if (entry.quantity != 1) {
                dropNumList.add(entry.quantity.toString())
            }

            if (entry.percentage != 100F) dropNumList.add(Text.translatable("cobblemon.ui.pokedex.info.drops_percentage", entry.percentage.toDouble()).string)

            var displayText = if (dropNumList.size != 0) {
                Text.translatable(
                    "cobblemon.ui.pokedex.info.drops_display",
                    Text.translatable(itemStack.translationKey),
                    dropNumList.joinToString(", ")
                )
            } else {
                Text.translatable(itemStack.translationKey)
            }

            drawScaledText(
                context = context,
                text = displayText,
                x = x + 10,
                y = y + 2,
                colour = 0x606B6E,
                scale = PokedexGUIConstants.SCALE
            )

            context.matrices.pop()
            context.matrices.pop()
        }

        override fun getNarration(): Text {
            return Text.translatable(entry.item.toTranslationKey())
        }

    }

}