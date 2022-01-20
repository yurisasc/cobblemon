package com.cablemc.pokemoncobbled.client.gui.summary

import com.cablemc.pokemoncobbled.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.client.gui.blitk
import com.cablemc.pokemoncobbled.client.gui.summary.widgets.ModelWidget
import com.cablemc.pokemoncobbled.client.gui.summary.widgets.PartyWidget
import com.cablemc.pokemoncobbled.client.gui.summary.widgets.pages.info.InfoWidget
import com.cablemc.pokemoncobbled.client.gui.summary.widgets.pages.stats.StatWidget
import com.cablemc.pokemoncobbled.client.gui.summary.widgets.pages.SummarySwitchButton
import com.cablemc.pokemoncobbled.client.gui.summary.widgets.pages.moves.MovesWidget
import com.cablemc.pokemoncobbled.client.storage.ClientParty
import com.cablemc.pokemoncobbled.common.api.moves.MoveSet
import com.cablemc.pokemoncobbled.common.api.reactive.Observable.Companion.emitWhile
import com.cablemc.pokemoncobbled.common.api.reactive.ObservableSubscription
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.TranslatableComponent
import java.security.InvalidParameterException

class Summary private constructor(): Screen(TranslatableComponent("pokemoncobbled.ui.summary.title")) {

    companion object {
        // Size of UI at Scale 1
        private const val BASE_WIDTH = 325
        private const val BASE_HEIGHT = 200

        // Page Numbers
        private const val INFO = 0
        private const val MOVES = 1
        private const val STATS = 2

        // Resources
        private val baseResource = cobbledResource("ui/summary/summary_base.png")
        private val displayBackgroundResource = cobbledResource("ui/summary/summary_display.png")
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

    constructor(party: ClientParty) : this() {
        party.forEach {
            pokemonList.add(it)
        }
        currentPokemon = pokemonList[PokemonCobbledClient.storage.selectedSlot]
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
        if(pokemonList.isEmpty()) {
            throw InvalidParameterException("Summary UI cannot display zero Pokemon")
        }
        if(pokemonList.size > 6) {
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
    private lateinit var currentPage: AbstractWidget

    /**
     * The Model display Widget
     */
    private lateinit var modelWidget: ModelWidget

    /**
     * Initializes the Summary Screen
     */
    override fun init() {
        super.init()

        val x = (width - BASE_WIDTH) / 2
        val y = (height - BASE_HEIGHT) / 2

        // Currently always starting with the MovesWidget
        currentPage = MovesWidget(
            pX = x, pY = y,
            pWidth = BASE_WIDTH, pHeight = BASE_HEIGHT,
            summary = this
        )

        // Add Buttons to change Pages - START
        addRenderableWidget(
            SummarySwitchButton(
                pX = x + 3, pY = y + 4,
                pWidth = 55, pHeight =  17,
                component = TranslatableComponent("pokemoncobbled.ui.info")
            ) {
            switchTo(INFO)
        })
        addRenderableWidget(
            SummarySwitchButton(
                pX = x + 62, pY = y + 4,
                pWidth = 55, pHeight = 17,
                component = TranslatableComponent("pokemoncobbled.ui.moves")
            ) {
            switchTo(MOVES)
        })
        addRenderableWidget(
            SummarySwitchButton(
                pX = x + 121, pY = y + 4,
                pWidth = 55, pHeight = 17,
                component = TranslatableComponent("pokemoncobbled.ui.stats")
            ) {
            switchTo(STATS)
        })
        // Add Buttons to change Pages - END

        // Add Exit Button
        addRenderableWidget(
            ExitButton(
                pX = x + 296, pY = y + 4,
                pWidth = 28, pHeight = 16,
                pXTexStart = 0, pYTexStart = 0, pYDiffText = 0
            ) {
            Minecraft.getInstance().setScreen(null)
        })

        // Add Party
        addRenderableWidget(
            PartyWidget(
                pX = x + BASE_WIDTH, pY = y,
                pWidth = BASE_WIDTH, pHeight = BASE_HEIGHT,
                pokemonList = pokemonList
            )
        )

        // Add Model Preview
        modelWidget = ModelWidget(
            pX = x + 183, pY = y + 24,
            pWidth = 104, pHeight = 97,
            pokemon = currentPokemon
        )
        addRenderableWidget(
            modelWidget
        )

        // Add CurrentPage
        addRenderableWidget(currentPage)
    }

    /**
     * Switches the selected PKM
     */
    private fun switchSelection(newSelection: Int) {
        pokemonList[newSelection]?.run {
            currentPokemon = this
        }
        moveSetSubscription?.unsubscribe()
        listenToMoveSet()
        modelWidget.pokemon = currentPokemon
    }

    private var moveSetSubscription: ObservableSubscription<MoveSet>? = null

    /**
     * Start observing the MoveSet of the current PKM for changes
     */
    private fun listenToMoveSet() {
        moveSetSubscription = currentPokemon.getMoveSetObservable()
            .pipe(emitWhile { isOpen() })
            .subscribe {
                if(currentPage is MovesWidget)
                    switchTo(MOVES)
            }
    }

    /**
     * Returns if this Screen is open or not
     */
    private fun isOpen() = Minecraft.getInstance().screen == this

    /**
     * Switches to the given Page
     */
    private fun switchTo(page: Int) {
        removeWidget(currentPage)
        when (page) {
            INFO -> {
                currentPage = InfoWidget(
                    pX = (width - BASE_WIDTH) / 2, pY = (height - BASE_HEIGHT) / 2,
                    pWidth = BASE_WIDTH, pHeight = BASE_HEIGHT
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
                    pX = (width - BASE_WIDTH) / 2, pY = (height - BASE_HEIGHT) / 2,
                    pWidth = BASE_WIDTH, pHeight =  BASE_HEIGHT
                )
            }
        }
        addRenderableWidget(currentPage)
    }

    override fun render(pMatrixStack: PoseStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        renderBackground(pMatrixStack)

        // Render Display Background
        blitk(
            poseStack = pMatrixStack,
            texture = displayBackgroundResource,
            x = (width - BASE_WIDTH) / 2, y = (height - BASE_HEIGHT) / 2,
            width = BASE_WIDTH, height = BASE_HEIGHT
        )

        // Render Base Resource
        blitk(
            poseStack = pMatrixStack,
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
    override fun isPauseScreen(): Boolean {
        return false
    }
}