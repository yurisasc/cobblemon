/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.gui.summary

import com.cablemc.pokemod.common.api.gui.blitk
import com.cablemc.pokemod.common.api.moves.MoveSet
import com.cablemc.pokemod.common.api.reactive.Observable.Companion.emitWhile
import com.cablemc.pokemod.common.api.reactive.ObservableSubscription
import com.cablemc.pokemod.common.api.scheduling.after
import com.cablemc.pokemod.common.client.PokemodClient
import com.cablemc.pokemod.common.client.gui.summary.widgets.EvolutionListScrollPane
import com.cablemc.pokemod.common.client.gui.summary.widgets.ModelWidget
import com.cablemc.pokemod.common.client.gui.summary.widgets.PartyWidget
import com.cablemc.pokemod.common.client.gui.summary.widgets.pages.SummarySwitchButton
import com.cablemc.pokemod.common.client.gui.summary.widgets.pages.info.InfoWidget
import com.cablemc.pokemod.common.client.gui.summary.widgets.pages.moves.MovesWidget
import com.cablemc.pokemod.common.client.gui.summary.widgets.pages.stats.StatWidget
import com.cablemc.pokemod.common.client.storage.ClientParty
import com.cablemc.pokemod.common.pokemon.Pokemon
import com.cablemc.pokemod.common.util.lang
import com.cablemc.pokemod.common.util.pokemodResource
import java.security.InvalidParameterException
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Drawable
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

class Summary private constructor(): Screen(Text.translatable("pokemod.ui.summary.title")) {

    companion object {
        // Size of UI at Scale 1
        private const val BASE_WIDTH = 325
        private const val BASE_HEIGHT = 200

        // Page Numbers
        private const val INFO = 0
        private const val MOVES = 1
        private const val STATS = 2

        // Resources
        private val baseResource = pokemodResource("ui/summary/summary_base.png")
        private val displayBackgroundResource = pokemodResource("ui/summary/summary_display.png")
    }

    constructor(vararg pokemon: Pokemon, editable: Boolean = true, selection: Int = 0) : this() {
        pokemon.forEach {
            pokemonList.add(it)
        }
        currentPokemon = pokemonList[selection]
            ?: pokemonList.filterNotNull().first()
        commonInit()
        this.editable = editable
    }

    var sidePanel: Element? = null
        set(value) {
            if (field != null) {
                remove(field)
            }
            field = value
            if (value is Drawable && value is Selectable) {
                addDrawableChild(value)
            }
        }

    constructor(party: ClientParty) : this() {
        party.forEach { pokemonList.add(it) }
        currentPokemon = pokemonList[PokemodClient.storage.selectedSlot]
            ?: pokemonList.filterNotNull().first()
        commonInit()
    }

    /**
     * The Pokémon that shall be displayed
     */
    private val pokemonList = mutableListOf<Pokemon?>()

    /**
     * Make sure that we have at least one Pokemon and not more than 6
     */
    private fun commonInit() {
        if (pokemonList.isEmpty()) {
            throw InvalidParameterException("Summary UI cannot display zero Pokemon")
        }
        if (pokemonList.size > 6) {
            throw InvalidParameterException("Summary UI cannot display more than six Pokemon")
        }
        listenToMoveSet()
    }

    /**
     * Whether you shall be able to edit Pokemon (Move reordering)
     */
    private var editable = true

    /**
     * The currently selected Pokemon
     */
    internal lateinit var currentPokemon: Pokemon

    /**
     * The current page being displayed
     */
    private lateinit var currentPage: ClickableWidget

    /**
     * The Model display Widget
     */
    private lateinit var modelWidget: ModelWidget

    var currentPageIndex = INFO

    fun `somethi$ng`() {

    }

    /**
     * Initializes the Summary Screen
     */
    public override fun init() {
        super.init()

        val x = (width - BASE_WIDTH) / 2
        val y = (height - BASE_HEIGHT) / 2


        currentPage = InfoWidget(
            pX = x, pY = y,
            pWidth = BASE_WIDTH, pHeight = BASE_HEIGHT,
            pokemon = currentPokemon
        )
        currentPageIndex = INFO

        addDrawableChild(
            SummaryButton(
                buttonX = x + 212F,
                buttonY = y + 172F,
                buttonWidth = SummaryButton.BUTTON_WIDTH * 1.5,
                buttonHeight = SummaryButton.BUTTON_HEIGHT * 1.5,
                clickAction = {
                    after(ticks = 0, serverThread = false) {
                        sidePanel = EvolutionListScrollPane(this.currentPokemon)
                    }
                },
                text = lang("ui.evolve"),
                renderRequirement = { this.currentPokemon.evolutionProxy.client().isNotEmpty() },
                clickRequirement = { this.currentPokemon.evolutionProxy.client().isNotEmpty() }
            )
        )


        // Add Buttons to change Pages - START
        addDrawableChild(
            SummarySwitchButton(
                pX = x + 3, pY = y + 6,
                pWidth = 55, pHeight =  17,
                component = Text.translatable("pokemod.ui.info")
            ) {
            switchTo(INFO)
        })
        addDrawableChild(
            SummarySwitchButton(
                pX = x + 62, pY = y + 6,
                pWidth = 55, pHeight = 17,
                component = Text.translatable("pokemod.ui.moves")
            ) {
            switchTo(MOVES)
        })
        addDrawableChild(
            SummarySwitchButton(
                pX = x + 121, pY = y + 6,
                pWidth = 55, pHeight = 17,
                component = Text.translatable("pokemod.ui.stats")
            ) {
            switchTo(STATS)
        })
        // Add Buttons to change Pages - END

        // Add Exit Button
        addDrawableChild(
            ExitButton(
                pX = x + 265, pY = y + 6,
                pWidth = 28, pHeight = 16,
                pXTexStart = 0, pYTexStart = 0, pYDiffText = 0
            ) {
            MinecraftClient.getInstance().setScreen(null)
        })

        // Add Party
        addDrawableChild(
            PartyWidget(
                pX = x + BASE_WIDTH - 2, pY = y + 18,
                pWidth = BASE_WIDTH, pHeight = BASE_HEIGHT,
                isParty = currentPokemon in PokemodClient.storage.myParty,
                summary = this,
                pokemonList = pokemonList
            )
        )

        // Add Model Preview
        modelWidget = ModelWidget(
            pX = x + 183, pY = y + 24,
            pWidth = 102, pHeight = 100,
            pokemon = currentPokemon.asRenderablePokemon(),
            baseScale = 2F
        )
        addDrawableChild(
            modelWidget
        )

        // Add CurrentPage
        addDrawableChild(currentPage)
    }

    /**
     * Switches the selected PKM
     */
    fun switchSelection(newSelection: Int) {
        pokemonList[newSelection]?.run {
            currentPokemon = this
        }
        moveSetSubscription?.unsubscribe()
        listenToMoveSet()
        switchTo(currentPageIndex)
        children().find { it is EvolutionListScrollPane }?.let(this::remove)
        modelWidget.pokemon = currentPokemon.asRenderablePokemon()
    }

    private var moveSetSubscription: ObservableSubscription<MoveSet>? = null

    /**
     * Start observing the MoveSet of the current PKM for changes
     */
    private fun listenToMoveSet() {
        moveSetSubscription = currentPokemon.moveSet.observable
            .pipe(emitWhile { isOpen() })
            .subscribe {
                if (currentPage is MovesWidget)
                    switchTo(MOVES)
            }
    }

    /**
     * Returns if this Screen is open or not
     */
    private fun isOpen() = MinecraftClient.getInstance().currentScreen == this

    /**
     * Switches to the given Page
     */
    private fun switchTo(page: Int) {
        currentPageIndex = page
        remove(currentPage)
        when (page) {
            INFO -> {
                currentPage = InfoWidget(
                    pX = (width - BASE_WIDTH) / 2, pY = (height - BASE_HEIGHT) / 2,
                    pWidth = BASE_WIDTH, pHeight = BASE_HEIGHT,
                    pokemon = this.currentPokemon
                )
            }
            MOVES -> {
                currentPage = MovesWidget(
                    pX = (width - BASE_WIDTH) / 2, pY = (height - BASE_HEIGHT) / 2,
                    pWidth = BASE_WIDTH, pHeight = BASE_HEIGHT,
                    summary = this
                )
            }
            STATS -> {
                currentPage = StatWidget(
                    pokemon = this.currentPokemon,
                    pX = (width - BASE_WIDTH) / 2, pY = (height - BASE_HEIGHT) / 2,
                    pWidth = BASE_WIDTH, pHeight =  BASE_HEIGHT
                )
            }
        }
        addDrawableChild(currentPage)
        ModelWidget.render = true
    }

    override fun render(pMatrixStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        renderBackground(pMatrixStack)

        // Render Display Background
        blitk(
            matrixStack = pMatrixStack,
            texture = displayBackgroundResource,
            x = (width - BASE_WIDTH) / 2, y = (height - BASE_HEIGHT) / 2,
            width = BASE_WIDTH, height = BASE_HEIGHT
        )

        // Render Base Resource
        blitk(
            matrixStack = pMatrixStack,
            texture = baseResource,
            x = (width - BASE_WIDTH) / 2, y = (height - BASE_HEIGHT) / 2,
            width = BASE_WIDTH, height = BASE_HEIGHT
        )

        // Render all added Widgets
        super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks)
    }

    /**
     * Whether this Screen should pause the Game in SinglePlayer
     */
    override fun shouldPause(): Boolean {
        return false
    }

    override fun mouseScrolled(d: Double, e: Double, f: Double): Boolean {
        return children().any { it.mouseScrolled(d, e, f) }
    }

    override fun mouseClicked(d: Double, e: Double, i: Int): Boolean {
        return children().any { it.mouseClicked(d, e, i) }
    }
}