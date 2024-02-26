/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.pokedex.widgets

import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUI
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.pokemon.Species
import com.cobblemon.mod.common.util.lang
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.ButtonWidget.PressAction
import net.minecraft.text.Text

class ScrollWidget(pX: Int, pY: Int
): SoundlessWidget(pX, pY, PokedexGUI.SCROLL_WIDTH, PokedexGUI.SCROLL_HEIGHT,  Text.literal("ScrollWidget")) {

    private var scrollSlots: MutableList<PokemonScrollSlot> = mutableListOf()
    private var scrollIndex: Int = 0
    private var pokedexSlotSpecies : MutableList<Species?> = mutableListOf()
    private lateinit var scrollBarWidget: ScrollBarWidget

    lateinit var parent: PokedexGUI

    init {
        scrollSlots.forEach { removeWidget(it) }
        scrollSlots.clear()

        for(i in 0 until PokedexGUI.SCROLL_SLOT_COUNT) pokedexSlotSpecies.add(null)

        for(i in 0 until PokedexGUI.SCROLL_SLOT_COUNT){
            scrollSlots.add(
                PokemonScrollSlot(
                    x + PokedexGUI.SPACER,
                    y + PokedexGUI.HEADER_HEIGHT + i * PokedexGUI.SCROLL_SLOT_HEIGHT,
                    PokedexGUI.SCROLL_WIDTH - PokedexGUI.SCROLL_BAR_WIDTH,
                    PokedexGUI.SCROLL_SLOT_HEIGHT
                )
            )

            addWidget(scrollSlots[i])
        }

        scrollBarWidget = ScrollBarWidget(
            pX + PokedexGUI.SCROLL_WIDTH - PokedexGUI.SCROLL_BAR_WIDTH,
            pY
        )
    }

    fun setPokemonInSlots(filteredPokedex: List<Species>){
        for(i in 0 until PokedexGUI.SCROLL_SLOT_COUNT){
            if(i + scrollIndex < filteredPokedex.size) {
                pokedexSlotSpecies[i] = filteredPokedex[i + scrollIndex]
                //Shouldn't be null due to above line
                scrollSlots[i].setPokemon(pokedexSlotSpecies[i]!!)
            } else {
                scrollSlots[i].removePokemon()
            }
        }
    }

    override fun renderButton(context: DrawContext, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        scrollSlots.forEach {
            it.render(context, pMouseX, pMouseY, pPartialTicks)
        }
    }

    fun setParent(p : PokedexGUI){
        parent = p
    }
}