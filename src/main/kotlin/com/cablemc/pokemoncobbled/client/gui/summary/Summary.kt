package com.cablemc.pokemoncobbled.client.gui.summary

import com.cablemc.pokemoncobbled.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.client.gui.blitk
import com.cablemc.pokemoncobbled.client.gui.summary.widgets.PartyWidget
import com.cablemc.pokemoncobbled.client.gui.summary.widgets.pages.info.InfoWidget
import com.cablemc.pokemoncobbled.client.gui.summary.widgets.pages.stats.StatWidget
import com.cablemc.pokemoncobbled.client.gui.summary.widgets.pages.SummarySwitchButton
import com.cablemc.pokemoncobbled.client.gui.summary.widgets.pages.moves.MovesWidget
import com.cablemc.pokemoncobbled.client.storage.ClientParty
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.resources.ResourceLocation
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
        private val baseResource = ResourceLocation(PokemonCobbled.MODID, "ui/summary/summary_base.png")
        private val displayBackgroundResource = ResourceLocation(PokemonCobbled.MODID, "ui/summary/summary_display.png")
    }

    constructor(vararg pokemon: Pokemon, selection: Int = 0) : this() {
        pokemon.forEach {
            pokemonList.add(it)
        }
        currentPokemon = pokemonList[selection]
            ?: pokemonList.filterNotNull().first()
        check()
    }

    constructor(party: ClientParty) : this() {
        party.forEach {
            pokemonList.add(it)
        }
        currentPokemon = pokemonList[PokemonCobbledClient.storage.selectedSlot]
            ?: pokemonList.filterNotNull().first()
        check()
    }

    /**
     * The Pokémon that shall be displayed
     */
    internal val pokemonList = mutableListOf<Pokemon?>()

    /**
     * Make sure that we have at least one Pokemon and not more than 6
     */
    private fun check() {
        if(pokemonList.isEmpty()) {
            throw InvalidParameterException("Summary UI cannot display zero Pokemon")
        }
        if(pokemonList.size > 6) {
            throw InvalidParameterException("Summary UI cannot display more than six Pokemon")
        }
    }

    /**
     * The currently selected Pokemon
     */
    internal lateinit var currentPokemon: Pokemon

    /**
     * The current page being displayed
     */
    private lateinit var currentPage: AbstractWidget

    /**
     * Initializes the Summary Screen
     */
    override fun init() {
        super.init()

        val x = (width - BASE_WIDTH) / 2
        val y = (height - BASE_HEIGHT) / 2

        // Currently always starting with the MovesWidget
        currentPage = MovesWidget(x, y, BASE_WIDTH, BASE_HEIGHT, this)

        // Add Buttons to change Pages - START
        addRenderableWidget(SummarySwitchButton(x + 3, y + 4, 55, 17, TranslatableComponent("pokemoncobbled.ui.info")) {
            switchTo(INFO)
        })
        addRenderableWidget(SummarySwitchButton(x + 62, y + 4, 55, 17, TranslatableComponent("pokemoncobbled.ui.moves")) {
            switchTo(MOVES)
        })
        addRenderableWidget(SummarySwitchButton(x + 121, y + 4, 55, 17, TranslatableComponent("pokemoncobbled.ui.stats")) {
            switchTo(STATS)
        })
        // Add Buttons to change Pages - END

        // Add Exit Button
        addRenderableWidget(ExitButton(x + 296, y + 4, 28, 16, 0, 0, 0) {
            Minecraft.getInstance().setScreen(null)
        })

        // Add Party
        addRenderableWidget(PartyWidget(x + BASE_WIDTH, y, BASE_WIDTH, BASE_HEIGHT, 6))

        // Add CurrentPage
        addRenderableWidget(currentPage)
    }

    /**
     * Switches to the given Page
     */
    private fun switchTo(page: Int) {
        removeWidget(currentPage)
        when (page) {
            INFO -> {
                currentPage = InfoWidget((width - BASE_WIDTH) / 2, (height - BASE_HEIGHT) / 2, BASE_WIDTH, BASE_HEIGHT)
            }
            MOVES -> {
                currentPage = MovesWidget((width - BASE_WIDTH) / 2, (height - BASE_HEIGHT) / 2, BASE_WIDTH, BASE_HEIGHT, this)
            }
            STATS -> {
                currentPage = StatWidget((width - BASE_WIDTH) / 2, (height - BASE_HEIGHT) / 2, BASE_WIDTH, BASE_HEIGHT)
            }
        }
        addRenderableWidget(currentPage)
    }

    override fun render(pMatrixStack: PoseStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        renderBackground(pMatrixStack)

        // Render Base Resource
        blitk(pMatrixStack, baseResource,
            (width - BASE_WIDTH) / 2, (height - BASE_HEIGHT) / 2,
            BASE_HEIGHT, BASE_WIDTH
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