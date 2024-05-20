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
import com.cobblemon.mod.common.api.pokedex.*
import com.cobblemon.mod.common.api.pokedex.filters.InvisibleFilter
import com.cobblemon.mod.common.api.pokedex.filters.SearchFilter
import com.cobblemon.mod.common.api.text.text
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
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.SEARCH_HEIGHT
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.SEARCH_WIDTH
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.SPACER
import com.cobblemon.mod.common.client.gui.pokedex.widgets.*
import com.cobblemon.mod.common.client.render.drawScaledTextJustifiedRight
import com.cobblemon.mod.common.pokedex.DexPokemonData
import com.cobblemon.mod.common.pokemon.FormData
import com.cobblemon.mod.common.pokemon.Species
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import org.spongepowered.asm.mixin.injection.Desc

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

        private val unknownText = Text.translatable("cobblemon.ui.pokedex.unknown")

        /**
         * Attempts to open this screen for a client.
         */
        fun open(pokedex: ClientPokedex) {
            val mc = MinecraftClient.getInstance()
            val screen = PokedexGUI(pokedex)
            mc.setScreen(screen)
        }
    }

    private var filteredPokedex : Collection<DexPokemonData> = mutableListOf()
    private var selectedPokemon : DexPokemonData? = null
    private var pokemonName = Text.translatable("")

    private lateinit var scrollScreen : EntriesScrollingWidget<EntriesScrollingWidget.PokemonScrollSlot>
    private lateinit var pokemonInfoWidget : PokemonInfoWidget
    private lateinit var descriptionWidget: DescriptionWidget
    private lateinit var formsWidget : FormsWidget
    private lateinit var searchWidget: SearchWidget

    public override fun init() {
        super.init()
        clearChildren()

        val x = (width - BASE_WIDTH) / 2
        val y = (height - BASE_HEIGHT) / 2

        //Info Widget
        if (::pokemonInfoWidget.isInitialized) remove(pokemonInfoWidget)
        pokemonInfoWidget = PokemonInfoWidget(x + SPACER * 2 + SCROLL_WIDTH, y + HEADER_HEIGHT)
        addDrawableChild(pokemonInfoWidget)

        //Description Widget
        if(::descriptionWidget.isInitialized) remove(descriptionWidget)
        descriptionWidget = DescriptionWidget( x + SPACER * 2 + SCROLL_WIDTH, y + HEADER_HEIGHT + SPACER + POKEMON_PORTRAIT_HEIGHT)
        addDrawableChild(descriptionWidget)

        if(::formsWidget.isInitialized) remove(formsWidget)
        formsWidget = FormsWidget(x + SPACER*3 + SCROLL_WIDTH + POKEMON_DESCRIPTION_WIDTH, y + HEADER_HEIGHT + SPACER + POKEMON_PORTRAIT_HEIGHT, ::setSelectedForm)
        addDrawableChild(formsWidget)

        if(::searchWidget.isInitialized) remove(searchWidget)
        searchWidget = SearchWidget(x, y - SPACER*2, SEARCH_WIDTH, SEARCH_HEIGHT, update = ::updateFilters)
        addDrawableChild(searchWidget)

        updateFilters()
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
            text = pokemonName,
            x = x + BASE_WIDTH - SPACER,
            y = y + HEADER_HEIGHT / 2
        )

        super.render(context, mouseX, mouseY, delta)
    }

    fun updateFilters() {
        val x = (width - BASE_WIDTH) / 2
        val y = (height - BASE_HEIGHT) / 2

        filteredPokedex = filterPokedex()

        //Scroll Screen
        if (::scrollScreen.isInitialized) remove(scrollScreen)
        scrollScreen = EntriesScrollingWidget(x + SPACER, y + HEADER_HEIGHT) { setSelectedPokemon(it) }
        scrollScreen.createEntries(filteredPokedex, pokedex)
        addDrawableChild(scrollScreen)

        if(filteredPokedex.isNotEmpty()){
            setSelectedPokemon(filteredPokedex.first())
        }
    }

    fun filterPokedex() : Collection<DexPokemonData> {
        return PokedexJSONRegistry.getSortedDexData(getFilters())
    }

    fun getFilters() : Collection<EntryFilter> {
        val filters: MutableList<EntryFilter> = mutableListOf()

        filters.add(InvisibleFilter(pokedex))
        filters.add(SearchFilter(pokedex, searchWidget.text))

        return filters
    }

    fun setSelectedPokemon(dexPokemonData : DexPokemonData){
        selectedPokemon = dexPokemonData
        val speciesEntry = pokedex.speciesEntries[selectedPokemon!!.name]
        pokemonInfoWidget.setPokemon(selectedPokemon!!, speciesEntry)

        val textToShowInDescription = mutableListOf<String>()

        if(speciesEntry != null && selectedPokemon!!.species != null){
            pokemonName = selectedPokemon!!.species!!.translatedName
            textToShowInDescription.addAll(selectedPokemon!!.species!!.pokedex)
        } else {
            pokemonName = Text.translatable(unknownText.string)
            textToShowInDescription.add(unknownText.string)
        }

        descriptionWidget.setText(textToShowInDescription)

        formsWidget.setForms(selectedPokemon!!.forms)
    }

    fun setSelectedForm(form: String){
        pokemonInfoWidget.setForm(form)
    }

    override fun shouldPause(): Boolean = true
}