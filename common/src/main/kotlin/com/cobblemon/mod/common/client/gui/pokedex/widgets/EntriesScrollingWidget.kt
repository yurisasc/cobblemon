/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.pokedex.widgets

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.gui.drawPortraitPokemon
import com.cobblemon.mod.common.api.gui.drawText
import com.cobblemon.mod.common.api.pokedex.SpeciesPokedexEntry
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.client.gui.ScrollingWidget
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.SCROLL_HEIGHT
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.SCROLL_SLOT_HEIGHT
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.SCROLL_WIDTH
import com.cobblemon.mod.common.client.gui.pokedex.widgets.EntriesScrollingWidget.PokemonScrollSlot
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.pokedex.DexPokemonData
import com.cobblemon.mod.common.pokemon.Species
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.lang
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.MutableText
import net.minecraft.text.Text

class EntriesScrollingWidget<PokemonScrollSlot : ScrollingWidget.Slot<EntriesScrollingWidget.PokemonScrollSlot>>(val pX: Int, val pY: Int, val setPokedexEntry: (Pair<DexPokemonData, SpeciesPokedexEntry?>) -> (Unit)
): ScrollingWidget<EntriesScrollingWidget.PokemonScrollSlot>(
    width = SCROLL_WIDTH,
    height = SCROLL_HEIGHT,
    left = pX,
    top = pY,
    slotHeight = SCROLL_SLOT_HEIGHT
){
    fun createEntries(filteredPokedex: List<Pair<DexPokemonData, SpeciesPokedexEntry?>>){
        filteredPokedex.forEach {
            val newEntry = PokemonScrollSlot (it){ selectPokemon(it) }
            addEntry( newEntry )
        }
    }

    override fun addEntry(entry: EntriesScrollingWidget.PokemonScrollSlot): Int {
        return super.addEntry(entry)
    }

    fun selectPokemon(entry: Pair<DexPokemonData, SpeciesPokedexEntry?>){
        setPokedexEntry.invoke(entry)
    }

    class PokemonScrollSlot(speciesEntryPair : Pair<DexPokemonData, SpeciesPokedexEntry?>, val setPokedexEntry : (Pair<DexPokemonData, SpeciesPokedexEntry?>) -> (Unit)
    ): Slot<PokemonScrollSlot>() {

        var pokemonName: MutableText = lang("default")
        var pokemonNumber: MutableText = "0".text()
        var pokemonSpecies: Species? = null
        var dexPokemonData: DexPokemonData? = null
        var speciesPokedexEntry: SpeciesPokedexEntry? = null

        companion object {
            private val scrollSlotResource = cobblemonResource("textures/gui/pokedex/scroll_slot_base.png")// Render Scroll Slot Background
        }

        init {
            dexPokemonData = speciesEntryPair.first
            pokemonSpecies = PokemonSpecies.getByIdentifier(speciesEntryPair.first.name)
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
            if(dexPokemonData == null) return false

            setPokedexEntry.invoke(Pair(dexPokemonData!!, speciesPokedexEntry))

            return true
        }

        override fun getNarration() = pokemonName
    }
}