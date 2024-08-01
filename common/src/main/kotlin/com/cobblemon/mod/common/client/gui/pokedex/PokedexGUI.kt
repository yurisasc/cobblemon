/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.pokedex

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.pokedex.*
import com.cobblemon.mod.common.api.pokedex.filters.InvisibleFilter
import com.cobblemon.mod.common.api.pokedex.filters.RegionFilter
import com.cobblemon.mod.common.api.pokedex.filters.SearchFilter
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.CobblemonSounds
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.sound.SoundEvent
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.BASE_HEIGHT
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.BASE_WIDTH
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.HALF_OVERLAY_WIDTH
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.HEADER_BAR_HEIGHT
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.SCALE
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.TAB_DESCRIPTION
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.TAB_ABILITIES
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.TAB_DROPS
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.TAB_ICON_SIZE
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.TAB_SIZE
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.TAB_STATS
import com.cobblemon.mod.common.client.gui.pokedex.widgets.*
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.pokedex.DexData
import com.cobblemon.mod.common.pokedex.DexPokemonData
import com.cobblemon.mod.common.pokemon.FormData
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Drawable
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import net.minecraft.util.Identifier

/**
 * Pokedex GUI
 *
 * @author JPAK
 * @since February 24, 2024
 */
class PokedexGUI private constructor(val pokedex: ClientPokedex, val type: String, val initSpecies: Identifier?): Screen(Text.translatable("cobblemon.ui.pokedex.title")) {

    companion object {
        private val screenBackground = cobblemonResource("textures/gui/pokedex/pokedex_screen.png")
        private val globeIcon = cobblemonResource("textures/gui/pokedex/globe_icon.png")
        private val caughtSeenIcon = cobblemonResource("textures/gui/pokedex/caught_seen_icon.png")
        private val arrowUpIcon = cobblemonResource("textures/gui/pokedex/arrow_up.png")
        private val arrowDownIcon = cobblemonResource("textures/gui/pokedex/arrow_down.png")

        private val tabSelectArrow = cobblemonResource("textures/gui/pokedex/select_arrow.png")
        private val tabIcons = arrayOf(
            cobblemonResource("textures/gui/pokedex/tab_info.png"),
            cobblemonResource("textures/gui/pokedex/tab_abilities.png"),
            cobblemonResource("textures/gui/pokedex/tab_size.png"),
            cobblemonResource("textures/gui/pokedex/tab_stats.png"),
            cobblemonResource("textures/gui/pokedex/tab_drops.png")
        )

        /**
         * Attempts to open this screen for a client.
         */
        fun open(pokedex: ClientPokedex, type: String, species: Identifier? = null) {
            val mc = MinecraftClient.getInstance()
            val screen = PokedexGUI(pokedex, type, species)
            mc.setScreen(screen)
        }
    }

    var initialDragPosX = 0.0
    var canDragRender = false

    private var filteredPokedex: Collection<DexPokemonData> = mutableListOf()
    private var seenCount = "0000"
    private var ownedCount = "0000"

    private var selectedPokemon: DexPokemonData? = null
    private var selectedForm: FormData? = null
    private var selectedRegion: DexData = PokedexJSONRegistry.getByName("national")!!

    private lateinit var scrollScreen: EntriesScrollingWidget
    private lateinit var pokemonInfoWidget: PokemonInfoWidget
    private lateinit var searchWidget: SearchWidget
    private lateinit var regionSelectWidgetUp: ScaledButton
    private lateinit var regionSelectWidgetDown: ScaledButton

    private val tabButtons: MutableList<ScaledButton> = mutableListOf()

    lateinit var tabInfoElement: Element
    var tabInfoIndex = TAB_DESCRIPTION

    override fun applyBlur(delta: Float) {}
    override fun renderDarkening(context: DrawContext) {}

    public override fun init() {
        super.init()
        clearChildren()

        val x = (width - BASE_WIDTH) / 2
        val y = (height - BASE_HEIGHT) / 2

        val unfilteredPokedex = PokedexJSONRegistry.getSortedDexData(mutableListOf())

        val ownedAmount = pokedex.getKnowledgeCount(PokedexEntryProgress.CAUGHT, unfilteredPokedex)
        ownedCount = ownedAmount.toString()
        while (ownedCount.length < 4) ownedCount = "0$ownedCount"

        seenCount = (ownedAmount + pokedex.getKnowledgeCount(PokedexEntryProgress.ENCOUNTERED, unfilteredPokedex)).toString()
        while (seenCount.length < 4) seenCount = "0$seenCount"

        //Info Widget
        if (::pokemonInfoWidget.isInitialized) remove(pokemonInfoWidget)
        pokemonInfoWidget = PokemonInfoWidget(x + 180, y + 28) { formData -> updatePokemonForm(formData) }
        addDrawableChild(pokemonInfoWidget)

        setUpTabs()

        //Tab Info Widget
        displaytabInfoElement(tabInfoIndex, false)

        if (::searchWidget.isInitialized) remove(searchWidget)
        searchWidget = SearchWidget(x + 26, y + 28, HALF_OVERLAY_WIDTH, HEADER_BAR_HEIGHT, update =::updateFilters)
        addDrawableChild(searchWidget)

        if (::regionSelectWidgetUp.isInitialized) remove(regionSelectWidgetUp)
        regionSelectWidgetUp = ScaledButton(
            buttonX = (x + 95).toFloat(),
            buttonY = (y + 14.5).toFloat(),
            buttonWidth = 8,
            buttonHeight = 6,
            scale = SCALE,
            resource = arrowUpIcon,
            clickAction = {
                selectedRegion = PokedexJSONRegistry.getByIdentifier(PokedexJSONRegistry.getNextDex(selectedRegion.identifier))!!
                updateFilters()
            })
        addDrawableChild(regionSelectWidgetUp)

        if (::regionSelectWidgetDown.isInitialized) remove(regionSelectWidgetDown)
        regionSelectWidgetDown = ScaledButton(
            buttonX = (x + 95).toFloat(),
            buttonY = (y + 19.5).toFloat(),
            buttonWidth = 8,
            buttonHeight = 6,
            scale = SCALE,
            resource = arrowDownIcon,
            clickAction = {
                selectedRegion = PokedexJSONRegistry.getByIdentifier(PokedexJSONRegistry.getPreviousDex(selectedRegion.identifier))!!
                updateFilters()
            })
        addDrawableChild(regionSelectWidgetDown)

        updateFilters(true)
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val matrices = context.matrices
        renderBackground(context, mouseX, mouseY, delta)

        val x = (width - BASE_WIDTH) / 2
        val y = (height - BASE_HEIGHT) / 2

        // Render Base Resource
        blitk(
            matrixStack = matrices,
            texture = cobblemonResource("textures/gui/pokedex/pokedex_base_${type}.png"),
            x = x, y = y,
            width = BASE_WIDTH,
            height = BASE_HEIGHT
        )

        blitk(
            matrixStack = matrices,
            texture = screenBackground,
            x = x, y = y,
            width = BASE_WIDTH,
            height = BASE_HEIGHT
        )

        // Region
        blitk(
            matrixStack = matrices,
            texture = globeIcon,
            x = (x + 26) / SCALE,
            y = (y + 15) / SCALE,
            width = 14,
            height = 14,
            scale = SCALE
        )

        // Region label
        drawScaledText(
            context = context,
            font = CobblemonResources.DEFAULT_LARGE,
            text = Text.translatable("cobblemon.ui.pokedex.region.${selectedRegion.identifier.path}").bold(),
            x = x + 36,
            y = y + 14,
            shadow = true
        )


        // Seen icon
        blitk(
            matrixStack = matrices,
            texture = caughtSeenIcon,
            x = (x + 252) / SCALE,
            y = (y + 15) / SCALE,
            width = 14,
            height = 14,
            vOffset = 0,
            textureHeight = 28,
            scale = SCALE
        )

        // Caught icon
        blitk(
            matrixStack = matrices,
            texture = caughtSeenIcon,
            x = (x + 290) / SCALE,
            y = (y + 15) / SCALE,
            width = 14,
            height = 14,
            vOffset = 14,
            textureHeight = 28,
            scale = SCALE
        )

        // Seen
        drawScaledText(
            context = context,
            font = CobblemonResources.DEFAULT_LARGE,
            text = seenCount.text().bold(),
            x = x + 262,
            y = y + 14,
            shadow = true
        )

        // Owned
        drawScaledText(
            context = context,
            font = CobblemonResources.DEFAULT_LARGE,
            text = ownedCount.text().bold(),
            x = x + 300,
            y = y + 14,
            shadow = true
        )

        // Tab arrow
        blitk(
            matrixStack = matrices,
            texture = tabSelectArrow,
            x = (x + 198 + (25 * tabInfoIndex)) / SCALE,
            // (x + 191.5 + (22 * tabInfoIndex)) / SCALE for 6 tabs
            y = (y + 177) / SCALE,
            width = 12,
            height = 6,
            scale = SCALE
        )

        super.render(context, mouseX, mouseY, delta)
    }

    override fun close() {
        playSound(CobblemonSounds.POKEDEX_CLOSE)
        super.close()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (::pokemonInfoWidget.isInitialized
            && pokemonInfoWidget.isWithinPortraitSpace(mouseX, mouseY)
            && pokedex.speciesEntries[selectedPokemon!!.identifier]?.highestDiscoveryLevel() != PokedexEntryProgress.NONE
        ) {
            canDragRender = true
            isDragging = true
            initialDragPosX = mouseX
            playSound(CobblemonSounds.POKEDEX_CLICK_SHORT)
        }
        return try {
            super.mouseClicked(mouseX, mouseY, button)
        } catch(e: ConcurrentModificationException) {
            false
        }
    }

    override fun mouseReleased(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        if (canDragRender) canDragRender = false
        if (isDragging) isDragging = false
        return super.mouseReleased(pMouseX, pMouseY, pButton)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        if (isDragging && canDragRender) {
            val dragOffsetY = ((initialDragPosX - mouseX) * 1).toFloat()
            pokemonInfoWidget.rotationY = (((pokemonInfoWidget.rotationY + dragOffsetY) % 360 + 360) % 360)
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
    }

    override fun tick() {
        if (::pokemonInfoWidget.isInitialized) pokemonInfoWidget.tick()
    }

    fun updateFilters(init: Boolean = false) {
        val x = (width - BASE_WIDTH) / 2
        val y = (height - BASE_HEIGHT) / 2

        filteredPokedex = filterPokedex()

        //Scroll Screen
        if (::scrollScreen.isInitialized) remove(scrollScreen)
        scrollScreen = EntriesScrollingWidget(x + 26, y + 39) { setSelectedPokemon(it) }
        scrollScreen.createEntries(filteredPokedex, pokedex)
        addDrawableChild(scrollScreen)

        if (filteredPokedex.isNotEmpty()) {
            if (init && initSpecies != null) {
                setSelectedPokemon(initSpecies)
            } else {
                setSelectedPokemon(filteredPokedex.first())
            }
        }
    }

    fun filterPokedex(): Collection<DexPokemonData> {
        return PokedexJSONRegistry.getSortedDexData(getFilters())
    }

    fun getFilters(): Collection<EntryFilter> {
        val filters: MutableList<EntryFilter> = mutableListOf()

        filters.add(InvisibleFilter(pokedex))
        filters.add(SearchFilter(pokedex, searchWidget.text))
        filters.add(RegionFilter(pokedex, selectedRegion))

        return filters
    }

    fun setSelectedPokemon(dexPokemonData: DexPokemonData) {
        selectedPokemon = dexPokemonData
        selectedForm = selectedPokemon!!.species!!.standardForm
        val speciesEntry = pokedex.speciesEntries[selectedPokemon!!.identifier]

        pokemonInfoWidget.setPokemon(selectedPokemon!!, speciesEntry)
        displaytabInfoElement(tabInfoIndex)
    }

    fun setSelectedPokemon(species: Identifier) {
        run loop@{
            for (dexData in filteredPokedex) {
                if (dexData.species == PokemonSpecies.getByIdentifier(species)) {
                    setSelectedPokemon(dexData)
                    return@loop
                }
            }
        }
    }

    fun setUpTabs() {
        val x = (width - BASE_WIDTH) / 2
        val y = (height - BASE_HEIGHT) / 2

        if (tabButtons.isNotEmpty()) tabButtons.clear()

        for (i in tabIcons.indices) {
            tabButtons.add(ScaledButton(
                x + 197F + (i * 25F), // x + 190.5F + (i * 22F) for 6 tabs
                y + 181.5F,
                TAB_ICON_SIZE,
                TAB_ICON_SIZE,
                resource = tabIcons[i],
                clickAction = { if (canSelectTab(i)) displaytabInfoElement(i) }
            ))
        }

        for (tab in tabButtons) addDrawableChild(tab)
    }

    fun displaytabInfoElement(tabIndex: Int, update: Boolean = true) {
        if (tabButtons.isNotEmpty() && tabButtons.size > tabIndex) {
            tabButtons.forEachIndexed { index, tab -> tab.isActive = index == tabIndex }
        }

        if (tabInfoIndex == TAB_ABILITIES && tabInfoElement is AbilitiesWidget) {
            remove((tabInfoElement as AbilitiesWidget).leftButton)
            remove((tabInfoElement as AbilitiesWidget).rightButton)
        }

        tabInfoIndex = tabIndex
        if (::tabInfoElement.isInitialized) remove(tabInfoElement)

        val x = (width - BASE_WIDTH) / 2
        val y = (height - BASE_HEIGHT) / 2

        when (tabIndex) {
            TAB_DESCRIPTION -> {
                tabInfoElement = DescriptionWidget( x + 180, y + 135)
            }
            TAB_ABILITIES -> {
                tabInfoElement = AbilitiesWidget( x + 180, y + 135)
            }
            TAB_SIZE -> {
                tabInfoElement = SizeWidget( x + 180, y + 135)
            }
            TAB_STATS -> {
                tabInfoElement = StatsWidget( x + 180, y + 135)
            }
            TAB_DROPS -> {
                tabInfoElement = DropsScrollingWidget(x + 189, y + 135)
            }
        }
        val element = tabInfoElement
        if (element is Drawable && element is Selectable) {
            addDrawableChild(element)
        }
    if (update) updatetabInfoElement()
    }

    fun updatetabInfoElement() {
        val speciesEntry = pokedex.speciesEntries[selectedPokemon!!.identifier]
        val textToShowInDescription = mutableListOf<String>()

        if (selectedPokemon!!.species != null
            && speciesEntry != null
            && speciesEntry.highestDiscoveryLevel() == PokedexEntryProgress.CAUGHT
        ) {
            val form = selectedForm ?: selectedPokemon!!.species!!.standardForm
            when (tabInfoIndex) {
                TAB_DESCRIPTION -> {
                    textToShowInDescription.addAll(selectedPokemon!!.species!!.pokedex)
                    (tabInfoElement as DescriptionWidget).showPlaceholder = false
                }
                TAB_ABILITIES -> {
                    (tabInfoElement as AbilitiesWidget).abilitiesList = form.abilities.map { ability -> ability.template }
                    (tabInfoElement as AbilitiesWidget).selectedAbilitiesIndex = 0
                    (tabInfoElement as AbilitiesWidget).setAbility()
                    (tabInfoElement as AbilitiesWidget).scrollAmount = 0.0

                    if ((tabInfoElement as AbilitiesWidget).abilitiesList.size > 1) {
                        addDrawableChild((tabInfoElement as AbilitiesWidget).leftButton)
                        addDrawableChild((tabInfoElement as AbilitiesWidget).rightButton)
                    }
                }
                TAB_SIZE -> {
                    if (::pokemonInfoWidget.isInitialized && pokemonInfoWidget.renderablePokemon != null) {
                        (tabInfoElement as SizeWidget).pokemonHeight = form.height
                        (tabInfoElement as SizeWidget).weight = form.weight
                        (tabInfoElement as SizeWidget).baseScale = form.baseScale
                        (tabInfoElement as SizeWidget).renderablePokemon = pokemonInfoWidget.renderablePokemon!!
                    }
                }
                TAB_STATS -> {
                    (tabInfoElement as StatsWidget).baseStats = form.baseStats
                }
                TAB_DROPS -> {
                    (tabInfoElement as DropsScrollingWidget).dropTable = form.drops
                    (tabInfoElement as DropsScrollingWidget).setEntries()
                }
//                TAB_MOVES -> {
//                    form.moves.getLevelUpMovesUpTo(100)
//                }
            }
        } else {
            if (tabInfoIndex != TAB_DESCRIPTION) displaytabInfoElement(TAB_DESCRIPTION)
            (tabInfoElement as DescriptionWidget).showPlaceholder = true
        }

        when (tabInfoIndex) {
            TAB_DESCRIPTION -> {
                (tabInfoElement as DescriptionWidget).setText(textToShowInDescription)
                (tabInfoElement as DescriptionWidget).scrollAmount = 0.0
            }
        }
    }

    fun updatePokemonForm(formData: FormData) {
        selectedForm = formData
        displaytabInfoElement(tabInfoIndex)
    }

    fun canSelectTab(tabIndex: Int): Boolean = (tabIndex != tabInfoIndex) && (pokedex.speciesEntries[selectedPokemon!!.identifier]?.highestDiscoveryLevel() == PokedexEntryProgress.CAUGHT)

    override fun shouldPause(): Boolean = false

    fun playSound(soundEvent: SoundEvent) {
        MinecraftClient.getInstance().soundManager.play(PositionedSoundInstance.master(soundEvent, 1.0F))
    }
}