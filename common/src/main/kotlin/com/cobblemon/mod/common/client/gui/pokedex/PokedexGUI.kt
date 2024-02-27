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
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.BASE_HEIGHT
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.BASE_WIDTH
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.HEADER_HEIGHT
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.POKEMON_DESCRIPTION_HEIGHT
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.POKEMON_DESCRIPTION_WIDTH
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.POKEMON_FORMS_HEIGHT
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.POKEMON_FORMS_WIDTH
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.POKEMON_PORTRAIT_HEIGHT
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.POKEMON_PORTRAIT_WIDTH
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.SCROLL_HEIGHT
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.SCROLL_WIDTH
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.SPACER
import com.cobblemon.mod.common.client.gui.pokedex.widgets.PokemonInfoWidget
import com.cobblemon.mod.common.client.gui.pokedex.widgets.ScrollWidget
import com.cobblemon.mod.common.client.render.drawScaledTextJustifiedRight
import com.cobblemon.mod.common.pokemon.Species
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.lang
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

/**
 * Pokedex GUI
 *
 * @author JPAK
 * @since February 24, 2024
 */
class PokedexGUI private constructor(val pokedex: ClientPokedex) : Screen(Text.translatable("cobblemon.ui.pokedex.title")) {

    companion object {

        private val baseResource = cobblemonResource("textures/gui/pokedex/pokedex_base.png")// Render Pokedex Background
        private val scrollBackgroundResource = cobblemonResource("textures/gui/pokedex/scroll_base.png")// Render Scroll Background
        private val pokemonPortraitBase = cobblemonResource("textures/gui/pokedex/pokemon_portrait.png")// Render Portrait Background
        private val pokemonDescriptionBase = cobblemonResource("textures/gui/pokedex/pokemon_description.png")// Render Description Background
        private val formsBase = cobblemonResource("textures/gui/pokedex/forms_base.png")// Render Form Background


        /**
         * Attempts to open this screen for a client.
         */
        fun open(pokedex: ClientPokedex) {
            val mc = MinecraftClient.getInstance()
            val screen = PokedexGUI(pokedex)
            mc.setScreen(screen)
        }
    }

    private var filteredPokedex : List<Pair<Species, SpeciesPokedexEntry?>> = mutableListOf()
    private lateinit var scrollScreen : ScrollWidget<ScrollWidget.PokemonScrollSlot>
    private lateinit var pokemonInfoWidget : PokemonInfoWidget
    private var selectedPokemon : Pair<Species, SpeciesPokedexEntry?>? = null

    public override fun init() {
        super.init()
        clearChildren()

        val x = (width - BASE_WIDTH) / 2
        val y = (height - BASE_HEIGHT) / 2

        filteredPokedex = filterPokedex()

        if (::scrollScreen.isInitialized) remove(scrollScreen)

        scrollScreen = ScrollWidget(x + SPACER, y + HEADER_HEIGHT) { setSelectedPokemon(it) }

        addDrawableChild(scrollScreen)
        scrollScreen.createEntries(filteredPokedex)

        if (::pokemonInfoWidget.isInitialized) remove(pokemonInfoWidget)

        pokemonInfoWidget = PokemonInfoWidget(x + SPACER * 2 + SCROLL_WIDTH, y + HEADER_HEIGHT)

        if(filteredPokedex.isNotEmpty()){
            pokemonInfoWidget.setPokemon(filteredPokedex[0])
        }
        addDrawableChild(pokemonInfoWidget)
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

    fun filterPokedex() : List<Pair<Species, SpeciesPokedexEntry?>> {
        return pokedex.getSortedEntries()
    }

    fun setSelectedPokemon(entry : Pair<Species, SpeciesPokedexEntry?>){
        selectedPokemon = entry
        pokemonInfoWidget.setPokemon(entry)
    }

    override fun shouldPause(): Boolean = true
}