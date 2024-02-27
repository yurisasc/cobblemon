/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.pokedex.widgets

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.gui.drawPortraitPokemon
import com.cobblemon.mod.common.api.pokedex.SpeciesPokedexEntry
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUI
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.SCROLL_BAR_WIDTH
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.SCROLL_HEIGHT
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.SCROLL_SLOT_HEIGHT
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.SCROLL_WIDTH
import com.cobblemon.mod.common.client.gui.pokedex.widgets.ScrollWidget.PokemonScrollSlot
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.pokemon.Species
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.lang
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
import net.minecraft.text.MutableText

class ScrollWidget<PokemonScrollSlot : AlwaysSelectedEntryListWidget.Entry<ScrollWidget.PokemonScrollSlot>>(val pX: Int, val pY: Int, val setPokedexEntry: (Pair<Species, SpeciesPokedexEntry?>) -> (Unit)
): AlwaysSelectedEntryListWidget<ScrollWidget.PokemonScrollSlot>(
    MinecraftClient.getInstance(),
    SCROLL_WIDTH,
    SCROLL_HEIGHT,
    pY,
    pY + SCROLL_HEIGHT,
    SCROLL_SLOT_HEIGHT
){
    private var scrollSlots : MutableList<ScrollWidget.PokemonScrollSlot> = mutableListOf()

    private var scrolling = false

    init {
        correctSize()
        setRenderHorizontalShadows(false)
        setRenderBackground(false)
        setRenderSelection(false)
    }

    private fun correctSize() {
        updateSize(SCROLL_WIDTH, SCROLL_HEIGHT, pY, pY + SCROLL_HEIGHT)
    }

    override fun updateSize(width: Int, height: Int, top: Int, bottom: Int) {
        super.updateSize(width, height, top, bottom)
        left = pX
        right = pX + width
    }

    fun createEntries(filteredPokedex: List<Pair<Species, SpeciesPokedexEntry?>>){
        filteredPokedex.forEach {
            val newEntry = PokemonScrollSlot (pX, it){ selectPokemon(it) }
            scrollSlots.add(newEntry)

            addEntry( newEntry )
        }
    }

    override fun addEntry(entry: ScrollWidget.PokemonScrollSlot): Int {
        return super.addEntry(entry)
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
        val entry: ScrollWidget.PokemonScrollSlot = this.getEntry(index)
        entry.render(
            context, index, y, x, entryWidth, entryHeight, mouseX, mouseY,
            hoveredEntry == entry, delta
        )
    }

    override fun renderList(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        val i = this.rowLeft
        val j = this.rowWidth
        val k = this.itemHeight
        val l = this.entryCount

        for (m in 0 until l) {
            val n = this.getRowTop(m)
            val o = this.getRowBottom(m)
            if (o >= this.top && n <= this.bottom) {
                this.renderEntry(context!!, mouseX, mouseY, delta, m, i, n, j, k)
            }
        }
    }

    fun selectPokemon(entry: Pair<Species, SpeciesPokedexEntry?>){
        setPokedexEntry.invoke(entry)
    }

    override fun getRowLeft(): Int {
        return this.left
    }

    override fun getRowRight(): Int {
        return this.rowLeft + this.rowWidth
    }

    override fun getRowWidth(): Int {
        return SCROLL_WIDTH - SCROLL_BAR_WIDTH
    }

    override fun getRowTop(index: Int): Int {
        return this.top - scrollAmount.toInt() + (index * this.itemHeight)
    }

    override fun getRowBottom(index: Int): Int {
        return this.getRowTop(index) + this.itemHeight
    }

    override fun getScrollbarPositionX() = pX + SCROLL_WIDTH - SCROLL_BAR_WIDTH
    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, partialTicks: Float) {
        correctSize()
        super.render(context, mouseX, mouseY, partialTicks)
    }


    class PokemonScrollSlot(val pX : Int, speciesEntryPair : Pair<Species, SpeciesPokedexEntry?>, val setPokedexEntry : (Pair<Species, SpeciesPokedexEntry?>) -> (Unit)
    ): Entry<PokemonScrollSlot>() {

        var pokemonName: MutableText = lang("default")
        var pokemonNumber: MutableText = "0".text()
        var pokemonSpecies: Species? = null
        var speciesPokedexEntry: SpeciesPokedexEntry? = null

        companion object {
            private val scrollSlotResource = cobblemonResource("textures/gui/pokedex/scroll_slot_base.png")// Render Scroll Slot Background
        }

        init {
            pokemonSpecies = speciesEntryPair.first
            //Shouldn't be null due to above line
            pokemonName = if(speciesEntryPair.second != null){
                pokemonSpecies!!.translatedName
            } else {
                lang("ui.pokedex.unknown")
            }

            pokemonNumber = "${pokemonSpecies!!.nationalPokedexNumber}".text()

            speciesPokedexEntry = speciesEntryPair.second
        }

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
            if(pokemonSpecies == null) return

            val matrices = context.matrices

            blitk(
                matrixStack = matrices,
                texture = scrollSlotResource,
                x = x,
                y = y,
                width = entryWidth,
                height = entryHeight
            )

            drawScaledText(
                context = context,
                text = pokemonName,
                x = x + entryHeight,
                y = y + 2
            )

            drawScaledText(
                context = context,
                text = pokemonNumber,
                x = x + entryHeight,
                y = y + entryHeight/2
            )

            context.enableScissor(
                x,
                y,
                x + entryHeight,
                y + entryHeight
            )

            matrices.push()
            matrices.translate(
                x.toDouble() + entryHeight.toDouble()/2,
                y.toDouble() + entryHeight.toDouble()/2 - 25,
                0.0
            )

            if (speciesPokedexEntry != null){
                drawPortraitPokemon(
                    pokemonSpecies!!,
                    mutableSetOf(),
                    matrices,
                    partialTicks = 0F,
                    scale = 11F,
                )
            } else {
                drawPortraitPokemon(
                    pokemonSpecies!!,
                    mutableSetOf(),
                    matrices,
                    partialTicks = 0F,
                    scale = 11F,
                    r = 0F,
                    g = 0F,
                    b = 0F
                )
            }

            matrices.pop()
            context.disableScissor()
        }

        override fun mouseClicked(d: Double, e: Double, i: Int): Boolean {
            if (isMouseOver(d, e)) {
                if(pokemonSpecies == null) return false

                setPokedexEntry.invoke(Pair(pokemonSpecies!!, speciesPokedexEntry))

                return true
            }
            return false
        }

        override fun getNarration() = pokemonName
    }
}