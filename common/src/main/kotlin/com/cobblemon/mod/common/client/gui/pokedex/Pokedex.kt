/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.pokedex
import com.cobblemon.mod.common.Cobblemon.LOGGER
import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.pokedex.ClientPokedex
import com.cobblemon.mod.common.api.pokedex.SpeciesPokedexEntry
import com.cobblemon.mod.common.client.gui.pokedex.widgets.PokemonScrollSlot
import com.cobblemon.mod.common.client.gui.pokedex.widgets.ScrollWidget
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.client.render.drawScaledTextJustifiedRight
import com.cobblemon.mod.common.pokemon.Species
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.lang
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import net.minecraft.util.Identifier

/**
 * Pokedex GUI
 *
 * @author JPAK
 * @since February 24, 2024
 */
class Pokedex private constructor(val pokedex: ClientPokedex) : Screen(Text.translatable("cobblemon.ui.pokedex.title")) {

    companion object {
        const val BASE_WIDTH = 350
        const val BASE_HEIGHT = 200
        const val HEADER_HEIGHT = 15
        const val SPACER = 5
        const val SCROLL_HEIGHT = 180
        const val SCROLL_WIDTH = 90
        const val POKEMON_PORTRAIT_HEIGHT = 105
        const val POKEMON_PORTRAIT_WIDTH = 245
        const val POKEMON_DESCRIPTION_WIDTH = 160
        const val POKEMON_DESCRIPTION_HEIGHT = 70
        const val POKEMON_FORMS_WIDTH = 80
        const val POKEMON_FORMS_HEIGHT = 70
        const val PORTRAIT_SIZE = 66
        const val SCALE = 0.5F
        const val SCROLL_SLOT_COUNT = 9
        const val SCROLL_SLOT_HEIGHT = SCROLL_HEIGHT / SCROLL_SLOT_COUNT
        const val SCROLL_BAR_WIDTH = 5

        private val baseResource = cobblemonResource("textures/gui/pokedex/pokedex_base.png")// Render Pokedex Background
        private val scrollBackgroundResource = cobblemonResource("textures/gui/pokedex/scroll_base.png")// Render Scroll Background
        private val scrollSlotResource = cobblemonResource("textures/gui/pokedex/scroll_slot_base.png")// Render Scroll Slot Background
        private val pokemonPortraitBase = cobblemonResource("textures/gui/pokedex/pokemon_portrait.png")// Render Portrait Background
        private val pokemonDescriptionBase = cobblemonResource("textures/gui/pokedex/pokemon_description.png")// Render Description Background
        private val formsBase = cobblemonResource("textures/gui/pokedex/forms_base.png")// Render Form Background


        /**
         * Attempts to open this screen for a client.
         */
        fun open(pokedex: ClientPokedex) {
            val mc = MinecraftClient.getInstance()
            val screen = Pokedex(pokedex)
            LOGGER.info(pokedex.speciesEntries)
            mc.setScreen(screen)
        }
    }

    private var index = 0
    private var scrollSlots: MutableList<PokemonScrollSlot> = mutableListOf()
    private var pokedexSlotSpecies : MutableList<Species?> = mutableListOf()
    private var filteredPokedex : List<Species> = mutableListOf()
    private lateinit var scrollScreen : ScrollWidget

    public override fun init() {
        super.init()
        clearChildren()

        val x = (width - BASE_WIDTH) / 2
        val y = (height - BASE_HEIGHT) / 2

        displayScroll(x, y)

        for(i in 0 until SCROLL_SLOT_COUNT) pokedexSlotSpecies.add(null)

        filteredPokedex = filterPokedex()
        setPokemonInSlots()
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val matrices = context.matrices
        renderBackground(context)

        val x = (width - BASE_WIDTH) / 2
        val y = (height - BASE_HEIGHT) / 2

        // Render Base Resource
        blitk(
            matrixStack = matrices,
            texture = baseResource,
            x = x, y = y,
            width = BASE_WIDTH,
            height = BASE_HEIGHT
        )

        blitk(
            matrixStack = matrices,
            texture = scrollBackgroundResource,
            x = x + SPACER,
            y = y + HEADER_HEIGHT,
            width = SCROLL_WIDTH,
            height = SCROLL_HEIGHT
        )


        for (i in 0 until SCROLL_SLOT_COUNT){
            blitk(
                matrixStack = matrices,
                texture = scrollSlotResource,
                x = x + SPACER,
                y = y + HEADER_HEIGHT + SCROLL_SLOT_HEIGHT*i,
                width = SCROLL_WIDTH - SCROLL_BAR_WIDTH,
                height = SCROLL_SLOT_HEIGHT
            )
        }

        blitk(
            matrixStack = matrices,
            texture = pokemonPortraitBase,
            x = x + SPACER * 2 + SCROLL_WIDTH,
            y = y + HEADER_HEIGHT,
            width = POKEMON_PORTRAIT_WIDTH,
            height = POKEMON_PORTRAIT_HEIGHT
        )

        blitk(
            matrixStack = matrices,
            texture = pokemonDescriptionBase,
            x = x + SPACER * 2 + SCROLL_WIDTH,
            y = y + HEADER_HEIGHT + POKEMON_PORTRAIT_HEIGHT + SPACER,
            width = POKEMON_DESCRIPTION_WIDTH,
            height = POKEMON_DESCRIPTION_HEIGHT
        )

        blitk(
            matrixStack = matrices,
            texture = formsBase,
            x = x + SPACER * 3 + SCROLL_WIDTH + POKEMON_DESCRIPTION_WIDTH,
            y = y + HEADER_HEIGHT + POKEMON_PORTRAIT_HEIGHT + SPACER,
            width = POKEMON_FORMS_WIDTH,
            height = POKEMON_FORMS_HEIGHT
        )


        drawScaledTextJustifiedRight(
            context = context,
            text = lang("species.pikachu.name"),
            x = x + BASE_WIDTH - SPACER,
            y = y + HEADER_HEIGHT / 2
        )

        super.render(context, mouseX, mouseY, delta)
    }

    private fun displayScroll(x: Int, y: Int){
        if (::scrollScreen.isInitialized) remove(scrollScreen)

        scrollScreen = ScrollWidget(x, y)

        addDrawableChild(scrollScreen)

        scrollSlots.clear()
        for(i in 0 until SCROLL_SLOT_COUNT){
            scrollSlots.add(PokemonScrollSlot(x, y + i * SCROLL_SLOT_HEIGHT, SCROLL_WIDTH, SCROLL_SLOT_HEIGHT))
            addDrawableChild(scrollSlots[i])
        }
    }

    fun setPokemonInSlots(){
        for(i in 0 until SCROLL_SLOT_COUNT){
            if(i + index >= filteredPokedex.size) {
                pokedexSlotSpecies[i] = filteredPokedex[i + index]
                //Shouldn't be null due to above line
                scrollSlots[i].setPokemon(pokedexSlotSpecies[i]!!)
            } else {
                scrollSlots[i].removePokemon()
            }
        }
    }

    fun filterPokedex() : List<Species> {
        return pokedex.sortedEntriesList
    }

    override fun shouldPause(): Boolean = true
}